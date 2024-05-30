package com.busanit.service;

import com.busanit.domain.MovieDTO;
import com.busanit.domain.MovieDetailDTO;
import com.busanit.domain.MovieStillCutDTO;
import com.busanit.entity.movie.Movie;
import com.busanit.entity.movie.MovieDetail;
import com.busanit.entity.movie.MovieStillCut;
import com.busanit.repository.MovieDetailRepository;
import com.busanit.repository.MovieRepository;
import com.busanit.repository.MovieStillCutRepository;
import com.busanit.entity.movie.Genre;
import com.busanit.entity.movie.MovieImage;
import com.busanit.repository.GenreRepository;
import com.busanit.util.GenreUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Getter
public class MovieService {

    private final OkHttpClient client = new OkHttpClient();
    private final MovieRepository movieRepository;
    private final MovieDetailRepository movieDetailRepository;
    private final MovieStillCutRepository movieStillCutRepository;
    private final GenreRepository genreRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${TMDB.apiKey}")
    private String apiKey;

    // 캐시를 사용하기 위한 데이터 구조
    private List<MovieDTO> cachedVideoMovies = new ArrayList<>();
    private List<MovieDTO> cachedAllMovies = new ArrayList<>();
    private List<MovieDTO> cachedHotMovies = new ArrayList<>();
    private LocalDate lastFetchDate = LocalDate.now().minusDays(1);

    // 상영작/상영예정작을 구분하기위한 로직중 개봉일자를 날짜타입에 맞추기위한 fomatter
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Scheduled(fixedRate = 43200000) // 12시간마다 데이터 갱신
    public void fetchAndStoreMovies() throws IOException {
        fetchAndStoreMoviesNowPlaying();
        fetchAndStoreMoviesUpcoming();
        fetchAndStoreMovieRuntimeAndReleaseData();
        fetchAndStoreMovieStillCuts();
        fetchAndStoreCertificationData();

        // 데이터 캐시 갱신
        cachedVideoMovies = getVideoMovies();
        cachedAllMovies = getAll();
        cachedHotMovies = getHotMovies();
        lastFetchDate = LocalDate.now();
    }

    /* 영화 현재상영목록 리스트 가져오는 API 및 저장 시작 */

    // API에서 받아온 현재상영목록 리스트에서 모든 영화 ID 추출하는 메서드
    // (나중에 다른 api 데이터들도 영화id를 기준으로 데이터를가져오기때문에 씀)
    public List<Long> getAllMovieIds() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream().map(Movie::getMovieId).collect(Collectors.toList());
    }

    public void fetchAndStoreMoviesNowPlaying() throws IOException {
        int totalPages = fetchTotalPages();
        for (int page = 1; page <= totalPages; page++) {
            String url = "https://api.themoviedb.org/3/movie/now_playing?language=ko-KR&page=" + page + "&api_key=" + apiKey + "&region=KR";
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                processResponse(responseBody);
            }
        }
    }

    // 상영예정작 DB에 넣기
    @Async
    public void fetchAndStoreMoviesUpcoming() throws IOException {
        for (int page = 1; page <= 8; page++) {
            String url = "https://api.themoviedb.org/3/movie/upcoming?language=ko-KR&page=" + page + "&api_key=" + apiKey + "&region=KR";
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                processResponse(responseBody);
            }
        }
    }

    private int fetchTotalPages() throws IOException { // 토탈페이지를 뽑는 함수
        String url = "https://api.themoviedb.org/3/movie/now_playing?language=ko-KR&page=1&api_key=" + apiKey + "&region=KR";
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("total_pages").asInt();
        }
    }

    //------------비디오 키 추출
    public String fetchMovieVideoKey(int movieId) throws IOException {
        // TMDB API URL을 포맷팅하여 생성합니다. 영화 ID와 API 키를 사용
        String url = String.format("https://api.themoviedb.org/3/movie/%d/videos?language=ko-KR&api_key=%s", movieId, apiKey);

        System.out.println("String.format key === " + url);
        // 요청을 생성
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            // 응답이 성공적인지 확인. 아니라면 예외 발생
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // 응답 바디에서 JSON을 파싱하여 결과 배열로
            JsonNode results = objectMapper.readTree(response.body().string()).get("results");
            if (results.isArray() && results.size() > 0) {
                // 첫 번째 비디오 정보를 가져옴
                JsonNode firstVideo = results.get(0);
                // 비디오의 키 값을 추출합니다.
                String videoKey = firstVideo.path("key").asText("");
                // 키 값이 비어있지 않은 경우, 출력하고 반환합니다.
                if (!videoKey.isEmpty()) {
                    System.out.println("Saving video key to database: " + videoKey);
                    return videoKey;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 비디오 키를 찾을 수 없는 경우 null을 반환합니다.
        return null;
    }

    private void processResponse(String responseBody) throws IOException {
        JsonNode results = getResultsFromResponse(responseBody);

        if (results.isArray()) {
            for (JsonNode node : results) {
                processMovieData(node);
            }
        }
    }

    // JSON 응답에서 results 배열을 추출
    private JsonNode getResultsFromResponse(String responseBody) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("results");
    }

    private void processMovieData(JsonNode node) throws IOException {
        // JSON 노드를 MovieDTO로 변환
        MovieDTO movieDTO = objectMapper.treeToValue(node, MovieDTO.class);
        Movie movie = getOrCreateMovie(movieDTO);
        updateMovieWithDTOInfo(movie, movieDTO);

        // 비디오 키를 가져옴
        String videoKey = fetchMovieVideoKey(Math.toIntExact(movieDTO.getId()));

        // 영화 상세 정보를 업데이트하거나 생성
        MovieDetail movieDetail = getOrCreateMovieDetail(movie);
        movieDetail.setVideo(videoKey);
        movieDetail.setPopularity(movieDTO.getPopularity());
        movieDetail.setReleaseDate(movieDTO.getReleaseDate());
        movie.setMovieDetail(movieDetail);

        // 장르 정보 처리
        processGenreData(movie, movieDTO);
        movieRepository.save(movie);
        // 영화 이미지 정보 처리
        processImageData(node, movie);

    }

    // Movie 객체를 MovieDTO 정보로 업데이트
    private void updateMovieWithDTOInfo(Movie movie, MovieDTO movieDTO) {
        movie.setMovieId(movieDTO.getId());
        movie.setTitle(movieDTO.getTitle());
        movie.setOverview(movieDTO.getOverview());
    }

    // Movie 객체를 가져오거나 새로 생성
    private Movie getOrCreateMovie(MovieDTO movieDTO) {
        return movieRepository.findById(movieDTO.getId()).orElse(new Movie());
    }

    // MovieDetail 객체를 가져오거나 새로 생성
    private MovieDetail getOrCreateMovieDetail(Movie movie) {
        MovieDetail movieDetail = movie.getMovieDetail();
        if (movieDetail == null || movieDetailRepository.findById(movieDetail.getMovieDetailId()).isEmpty()) {
            movieDetail = new MovieDetail();
        }
        return movieDetail;
    }

    //영화 장르 데이터 처리
    private void processGenreData(Movie movie, MovieDTO movieDTO) {

        List<Genre> existingGenres = movie.getGenres();

        for (Integer genreId : movieDTO.getGenreIds()) {
            String genreName = GenreUtils.getGenreName(genreId); // ID를 한글 이름으로 변환
            // 영화에 이미 해당 장르가 할당되어 있는지 확인합니다.
            boolean isGenreAlreadyAssigned = existingGenres.stream()
                    .anyMatch(genre -> genre.getGenreName().equals(genreName));

            // 이미 할당된 장르가 아니라면 데이터베이스에서 조회하거나 새로 생성합니다.
            if (!isGenreAlreadyAssigned) {
                Genre genre = genreRepository.findByGenreName(genreName)
                        .orElseGet(() -> {
                            Genre newGenre = new Genre();
                            newGenre.setGenreName(genreName); // 장르 이름 설정
                            return genreRepository.save(newGenre); // 데이터베이스에 저장
                        });
                movie.addGenre(genre); // 영화에 장르 추가

            }
        }
    }


    public void fetchAndStoreMovieRuntimeAndReleaseData() throws IOException {
        List<Long> movieIds = getAllMovieIds();
        for (Long movieId : movieIds) {
            String url = "https://api.themoviedb.org/3/movie/" + movieId + "?language=ko-KR&api_key=" + apiKey;
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                processRuntimeAndReleaseDataResponse(responseBody);
            }
        }
    }


    public void processRuntimeAndReleaseDataResponse(String responseBody) throws IOException {
        MovieDetailDTO movieDetailDTO = objectMapper.readValue(responseBody, MovieDetailDTO.class);
        System.out.println("responseBody = " + responseBody);
        Movie movie = movieRepository.findById(movieDetailDTO.getId()).orElse(new Movie());
        MovieDetail movieDetail = getOrCreateMovieDetail(movie);
        movieDetail.setReleaseDate(movieDetailDTO.getRelease_date());
        movieDetail.setRuntime(movieDetailDTO.getRuntime());
        movieDetailRepository.save(movieDetail);
    }

//    https://api.themoviedb.org/3/movie/653346?language=ko-KR&api_key=547e2cd4d0e26e68fb907dafef4f90ac

    public void fetchAndStoreMovieStillCuts() throws IOException {

        List<Long> movieIds = getAllMovieIds();
        for (Long movieId : movieIds) {
            String url = "https://api.themoviedb.org/3/movie/" + movieId + "/images?include_image_language=kr%2Cnull&language=KR&api_key=" + apiKey;
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                processStillCutsResponse(responseBody);
            }
        }
    }

    public void processStillCutsResponse(String responseBody) throws IOException {
        MovieStillCutDTO movieStillCutDTO = objectMapper.readValue(responseBody, MovieStillCutDTO.class);

//        List<String> filePaths = new ArrayList<>();
        if (movieStillCutDTO.getBackdrops() != null) {
            for (MovieStillCutDTO.ImageDTO backdrop : movieStillCutDTO.getBackdrops()) {
                saveSingleStillCut(movieStillCutDTO.getId(), backdrop.getFile_path());
            }
        }
        if (movieStillCutDTO.getPosters() != null) {
            for (MovieStillCutDTO.ImageDTO poster : movieStillCutDTO.getPosters()) {
                saveSingleStillCut(movieStillCutDTO.getId(), poster.getFile_path());
            }
        }

    }

    private void saveSingleStillCut(Long movieId, String filePath) {
        if (movieStillCutRepository.existsByMovies_movieIdAndStillCuts(movieId, filePath)) {
            return; // 스틸컷 중복체크
        }
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new IllegalArgumentException("Invalid movieId: " + movieId));
        MovieStillCut movieStillCut = new MovieStillCut();
//        movieStillCut.setMovieStillCutId(movieId);
        movieStillCut.setStillCuts(filePath); // 여기서는 각각의 filePath를 별도로 저장합니다.
        movie.addStillCut(movieStillCut);
        movieStillCutRepository.save(movieStillCut);
//        movieRepository.save(movie);
    }


    private boolean hasImage(List<MovieImage> images, String posterPath, String backdropPath) {
        for (MovieImage image : images) {
            if (image.getPosterPath().equals(posterPath) && image.getBackdropPath().equals(backdropPath)) {
                return true;
            }
        }
        return false;
    }

    private void processImageData(JsonNode node, Movie movie) {
        Optional<Movie> optionalMovie = movieRepository.findById(movie.getMovieId());
        if (!optionalMovie.isPresent()) {
            System.out.println("해당 ID의 영화가 존재하지 않습니다.");
            return;
        }
        Movie existingMovie = optionalMovie.get();


        String posterPath = node.get("poster_path").asText();
        String backdropPath = node.get("backdrop_path").asText();

        // 서비스 계층에서의 이미지 중복 검사
        if (!hasImage(existingMovie.getImages(), posterPath, backdropPath)) {
            MovieImage movieImage = new MovieImage();
            movieImage.setPosterPath(posterPath);
            movieImage.setBackdropPath(backdropPath);

            existingMovie.addImage(movieImage);

            // 수정된 movie를 저장
        }
    }

    public void fetchAndStoreCertificationData() throws IOException {
        List<Long> movieIds = getAllMovieIds(); // 모든 movie ID를 가져오는 메서드
        for (Long movieId : movieIds) {
            String url = "https://api.themoviedb.org/3/movie/" + movieId + "/release_dates?api_key=" + apiKey;
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                processCertificationResponse(responseBody, movieId);
            }
        }
    }

    public void processCertificationResponse(String responseBody, Long movieId) throws IOException {
        MovieDetailDTO.ReleaseDatesDTO releaseDatesDTO = objectMapper.readValue(responseBody, MovieDetailDTO.ReleaseDatesDTO.class);

        String certification = releaseDatesDTO.getResults().stream()
                .filter(result -> "KR".equals(result.getIso_3166_1()))
                .flatMap(result -> result.getRelease_dates().stream())
                .map(MovieDetailDTO.ReleaseDateInfo::getCertification)
                .findFirst()
                .orElse(null);

        if ("".equals(certification)) {
            certification = "-";
        } else if ("18".equals(certification)) {
            certification = "18세 이상 관람가";
        } else if ("15".equals(certification)) {
            certification = "15세 이상 관람가";
        } else if ("12".equals(certification)) {
            certification = "12세 이상 관람가";
        } else if ("ALL".equals(certification) || "All".equals(certification)) {
            certification = "전체 관람가";
        }


        if (certification != null) {
            Movie movie = movieRepository.findById(movieId).orElse(new Movie());
            MovieDetail movieDetail = getOrCreateMovieDetail(movie);
            movieDetail.setCertification(certification); // MovieDetail에 certification 세팅
            movieDetailRepository.save(movieDetail);
        }
    }

    //상영 중인 전체 영화
    public List<MovieDTO> getAll() {
        List<Movie> movieList = movieRepository.findAll();
        return movieList.stream().map(MovieDTO::convertToDTO)
                .collect(Collectors.toList());
    }

    // 상영중 영화 목록 더보기 화면 페이징 및 정렬
    public Page<MovieDTO> getMoviesPagingAndSorting(int page, int size, boolean isUpcoming) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        if (isUpcoming) {
            startDate = today;
            endDate = today.plusMonths(4);
        } else {
            startDate = today.minusMonths(4);
            endDate = today;
        }

        String startDateString = startDate.format(formatter);
        String endDateString = endDate.format(formatter);

        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> movieList = movieRepository.findAllByReleaseDateBetween(startDateString, endDateString, pageable);
        return movieList.map(MovieDTO::convertToDTO);
    }
    // 상영예정작 전체목록보기
    public Page<MovieDTO> getUpcomingMoviesPagingAndSorting(int page, int size) {
        return getMoviesPagingAndSorting(page, size, true);
    }
    // 상영작 전체목록보기
    public Page<MovieDTO> getCurrentMoviesPagingAndSorting(int page, int size) {
        return getMoviesPagingAndSorting(page, size, false);
    }

    //인기순 영화 정렬
    public List<MovieDTO> getHotMovies() {
        List<Movie> movieList = movieRepository.findAllByOrderByMovieDetailPopularityDesc();
        return movieList.stream().map(MovieDTO::convertToDTO)
                .collect(Collectors.toList());
    }

    //인기순 영화 중 영상있는 것
    public List<MovieDTO> getVideoMovies() {
        Pageable topFive = PageRequest.of(0, 5);
        List<Movie> movieList = movieRepository.findByVideoTrueOrderByPopularityDesc(topFive);
        return movieList.stream().map(MovieDTO::convertToDTO)
                .collect(Collectors.toList());
    }

    //영화 상세보기
    public List<MovieDTO> getMovieDetailInfo(Long movieId) {
        Optional<Movie> movieList = movieRepository.findById(movieId);
        return movieList.stream().map(MovieDTO::convertToDTO)
                .collect(Collectors.toList());
    }

    //로그인되어있는 유저 email받아오기
    public String getUserEmail() {
        String userEmail = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            userEmail = authentication.getName(); // 현재 로그인한 사용자의 이메일
        }
        return userEmail;
    }

    //모든 영화에서 개봉일자가 4개월 전/후 로 필터링 하는 함수
    //매개변수의 boolean 값으로 4개월 전으로 나눌지 4개월 후로 나눌지 선택할수있음!
    public List<MovieDTO> getFilteredMovies(List<MovieDTO> allMovies, boolean isUpcoming) {
        LocalDate referenceDate = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        if (isUpcoming) {
            startDate = referenceDate;
            endDate = referenceDate.plusMonths(4);
        } else {
            startDate = referenceDate.minusMonths(4);
            endDate = referenceDate;
        }

        return allMovies.stream()
                .filter(movie -> {
                    String releaseDateString = movie.getReleaseDate();
                    if (releaseDateString != null && !releaseDateString.isEmpty()) {
                        LocalDate releaseDate = LocalDate.parse(releaseDateString, formatter);
                        return !releaseDate.isBefore(startDate) && !releaseDate.isAfter(endDate);
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    // 검색기능
    public List<MovieDTO> searchMovies(String query) {
        List<Movie> searchResults = movieRepository.findByTitleContaining(query);
        return searchResults.stream().map(MovieDTO::convertToDTO)
                .collect(Collectors.toList());
    }

}
