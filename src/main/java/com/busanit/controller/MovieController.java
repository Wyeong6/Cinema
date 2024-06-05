package com.busanit.controller;

import com.busanit.domain.MovieDTO;
import com.busanit.entity.movie.Genre;
import com.busanit.entity.movie.Movie;
import com.busanit.entity.movie.MovieStillCut;
import com.busanit.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService2;
    private final ResourceLoader resourceLoader;

    @Value("${upload.path}")
    private String uploadPath;

    @Transactional
    @GetMapping("/")
    public String getDetailMovies(Model model) {

        //비디오가 있는 인기순영화
        List<MovieDTO> videoMovies = movieService2.getCachedVideoMovies();
        model.addAttribute("videoMovies", videoMovies);

        //모든 영화
        List<MovieDTO> allMovies = movieService2.getCachedAllMovies();

        //모든 영화에서 개봉일자가 4개월 전 부터 오늘날짜 까지인거만 가져옴 즉 현재 상영작임.
        List<MovieDTO> filteredMovies = movieService2.getFilteredMovies(allMovies,false);
        model.addAttribute("allMovies", filteredMovies);

        //모든 영화에서 개봉일자가 오늘날짜부터 4개월 후 까지인거만 가져옴 즉 상영예정작(개봉예정)임.
        List<MovieDTO> filteredUpcomingMovies = movieService2.getFilteredMovies(allMovies, true);
        model.addAttribute("upcomingMovies", filteredUpcomingMovies);

        //인기순
        List<MovieDTO> hotMovies = movieService2.getCachedHotMovies();
        model.addAttribute("hotMovies", hotMovies);

        return "main";
    }

    //현재 상영작 페이지

    @GetMapping("/nowMovies")
    public String nowMovies(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "12") int size) {
        Page<MovieDTO> moviePage = movieService2.getCurrentMoviesPagingAndSorting(page, size);
        model.addAttribute("moviePage", moviePage);
        return "movie/movie_list_now";
    }

    @GetMapping("/comingMovies")
    public String upcomingMovies(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "12") int size) {
        Page<MovieDTO> upcomingMoviesPage = movieService2.getUpcomingMoviesPagingAndSorting(page, size);
        model.addAttribute("moviePage", upcomingMoviesPage);
        return "movie/movie_list_comming";
    }

    //디테일페이지
    @GetMapping("/movies/{movieId}")
    public String movieDetailinfo(@PathVariable("movieId") Long movieId, Model model) {
        List<MovieDTO> movieInfos = movieService2.getMovieDetailInfo(movieId);
        System.out.println("movieInfos === " + movieInfos);
        String userEmail = movieService2.getUserEmail();

        model.addAttribute("movieInfos", movieInfos);
        model.addAttribute("movieId", movieId);
        model.addAttribute("userEmail", userEmail);

        return "movie/movie_get";
    }

    @GetMapping("/upcoming/{movieId}")
    public String upcomingDetailinfo(@PathVariable("movieId") Long movieId, Model model) {
        List<MovieDTO> movieInfos = movieService2.getMovieDetailInfo(movieId);
        System.out.println("movieInfos === " + movieInfos.get(0).getStillCutPaths());
        String userEmail = movieService2.getUserEmail();

        model.addAttribute("movieInfos", movieInfos);
        model.addAttribute("movieId", movieId);
        model.addAttribute("userEmail", userEmail);


        return "movie/movie_get";
    }


    @GetMapping("/movies/get")
    public String test(Model model) {
        return "movie/movie_get";
    }

    // 리뷰작성 모달
    @RequestMapping("/review/{movieId}")
    public String reviewPopup(@PathVariable("movieId") String movieId, Model model) {
        model.addAttribute("movieId", movieId);
        return "movie/review_modal";
    }

    // 검색 기능
    @GetMapping("/search")
    public String searchMovies(@RequestParam("query") String query, Model model) {
        List<MovieDTO> searchResults = movieService2.searchMovies(query);
        model.addAttribute("searchResults", searchResults);
        return "movie/movie_search";
    }

    // 영화 등록 (어드민페이지에서)
    @PostMapping("/movies/regist")
    public ResponseEntity<String> registMovie(
            @RequestParam("id") Long movieId,
            @RequestParam("title") String movieTitle,
            @RequestParam("overview") String movieOverview,
            @RequestParam("certifications") String certifications,
            @RequestParam("releaseDate") String movieReleaseDate,
            @RequestParam("RegisteredPoster") MultipartFile registeredPoster,
            @RequestParam("RegisteredBackdrop") MultipartFile registeredBackdrop,
            @RequestParam("runtime") String runtime,
            @RequestParam("video") String video,
            @RequestParam("genres") List<String> genres,
            @RequestParam("RegisteredStillCut") List<MultipartFile> registeredStillCut,
            Model model
    ) throws IOException {


        String stillCutRelativeUploadDir = "uploads/stillCuts/";
        String backdropRelativeUploadDir = "uploads/backdrops/";
        String posterRelativeUploadDir = "uploads/posters/";

        // 실제 파일 시스템 경로를 설정합니다.
        String uploadDirectory = resourceLoader.getResource("classpath:/static").getFile().getAbsolutePath();

        File stillCutDir = new File(uploadDirectory + "/" + stillCutRelativeUploadDir);
        File backdropDir = new File(uploadDirectory + "/" + backdropRelativeUploadDir);
        File posterDir = new File(uploadDirectory + "/" + posterRelativeUploadDir);
        // 디렉터리가 존재하지 않으면 생성합니다.
        if (!stillCutDir.exists()) {
            stillCutDir.mkdirs();
        }
        if (!backdropDir.exists()) {
            backdropDir.mkdirs();
        }
        if (!posterDir.exists()) {
            posterDir.mkdirs();
        }



        List<String> stillCutFiles = new ArrayList<>();
        String posterRelativeFilePath = "";
        String backdropRelativeFilePath = "";

        try {
            // 스틸컷 이미지를 저장
            for (MultipartFile file : registeredStillCut) {
                String fileName = file.getOriginalFilename();
                String relativeFilePath = stillCutRelativeUploadDir + fileName;
                String filePath = uploadDirectory + File.separator + stillCutRelativeUploadDir + fileName;

                // 파일을 지정된 경로에 저장합니다.
                file.transferTo(new File(filePath));
                stillCutFiles.add(relativeFilePath);
            }
            // 포스터 이미지를 저장
            String posterFileName = registeredPoster.getOriginalFilename();
            posterRelativeFilePath = posterRelativeUploadDir + posterFileName;
            String posterFilePath = uploadDirectory + File.separator + posterRelativeFilePath;

            // 파일을 지정된 경로에 저장합니다.
            registeredPoster.transferTo(new File(posterFilePath));

            // 백드롭 이미지를 저장
            String backdropFileName = registeredBackdrop.getOriginalFilename();
            backdropRelativeFilePath = backdropRelativeUploadDir + backdropFileName;
            String backdropFilePath = uploadDirectory + File.separator + backdropRelativeFilePath;

            // 파일을 지정된 경로에 저장합니다.
            registeredBackdrop.transferTo(new File(backdropFilePath));

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "파일 저장 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 중 오류가 발생했습니다.");
        }

        try {
            movieService2.saveMovie(
                    movieId, movieTitle, movieOverview, movieReleaseDate, certifications,
                    posterRelativeFilePath, backdropRelativeFilePath, stillCutFiles, genres, video, runtime
            );
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 영화 ID입니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }

        model.addAttribute("message", "영화 정보가 성공적으로 등록되었습니다.");
        return ResponseEntity.ok("영화 정보가 성공적으로 등록되었습니다.");
    }

    // 아이디 중복확인 버튼용
    @GetMapping("/movies/checkId")
    @ResponseBody
    public Map<String, Boolean> checkMovieId(@RequestParam("id") String id) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = movieService2.checkIfMovieIdExists(id);
        response.put("exists", exists);
        return response;
    }

}







