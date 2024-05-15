package com.busanit.service;

import com.busanit.domain.MovieDTO;
import com.busanit.domain.MovieDetailDTO;
import com.busanit.entity.movie.Genre;
import com.busanit.entity.movie.Movie;
import com.busanit.entity.movie.MovieDetail;
import com.busanit.entity.movie.MovieImage;
import com.busanit.repository.GenreRepository;
import com.busanit.repository.MovieDetailRepository;
import com.busanit.repository.MovieRepository;
import com.busanit.util.GenreUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieService {

    private final OkHttpClient client = new OkHttpClient();
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final MovieDetailRepository movieDetailRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${TMDB.apiKey}")
    private String apiKey;


    /* 영화 현재상영목록 리스트 가져오는 API 및 저장 시작 */

    @Scheduled(cron = "0 0 * * * *") // 1시간마다 메소드 실행
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
        movieDetail.setVoteAverage(movieDTO.getVoteAverage());
        movieDetail.setPopularity(movieDTO.getPopularity());
        movie.setMovieDetail(movieDetail);

        // 장르 정보 처리
        processGenreData(movie, movieDTO);

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

    // MovieImage 객체 생성 및 posterPath 설정
    private void processImageData(JsonNode node, Movie movie) {

        MovieImage movieImage = new MovieImage();
        // API 응답에서 poster_path 값 추출 및 설정
        String posterPath = node.get("poster_path").asText();
        String backdropPath = node.get("backdrop_path").asText();

        movieImage.setPosterPath(posterPath);
        movieImage.setBackdropPath(backdropPath);

        // MovieImage를 movie 인스턴스에 연결
        movie.addImage(movieImage);

        movieRepository.save(movie);
    }

    public List<MovieDTO> getAllMovies() {

        List<Movie> movieList = movieRepository.findAll();
        return movieList.stream().map(MovieDTO::convertToDTO)
                .collect(Collectors.toList());
    }

}
