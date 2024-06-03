package com.busanit.controller;

import com.busanit.domain.MovieDTO;
import com.busanit.entity.movie.Genre;
import com.busanit.entity.movie.Movie;
import com.busanit.entity.movie.MovieStillCut;
import com.busanit.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService2;

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
        System.out.println("movieInfos === " + movieInfos);
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
    @PostMapping("admin/movies/regist")
    public String registMovie(
            @RequestParam("id") Long movieId,
            @RequestParam("title") String movieTitle,
            @RequestParam("overview") String movieOverview,
            @RequestParam("certifications") String certifications,
            @RequestParam("releaseDate") String movieReleaseDate,
            @RequestParam("posterPath") String posterImage,
            @RequestParam("backdropPath") String backdropImage,
            @RequestParam("runtime") String runtime,
            @RequestParam("video") String video,
            @RequestParam("genres") List<String> genres,
            @RequestParam("RegisteredStillCut") List<MultipartFile> registeredStillCut,
            Model model
    ) {

        String uploadDir = "C:/uploads/stillCuts/";

        // 디렉터리가 존재하지 않으면 생성합니다.
        File dir = new File(uploadDir);
        try {
            // mkdirs() 메서드를 호출할 때 예외를 처리합니다.
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("디렉터리 생성 성공: " + uploadDir);
            } else {
                System.out.println("디렉터리 생성 실패 또는 이미 존재함: " + uploadDir);
            }
        } catch (SecurityException e) {
            // 권한 문제 등으로 디렉터리를 생성할 수 없는 경우를 처리합니다.
            System.err.println("디렉터리 생성 중 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }


        System.out.println("dir 체크 === " + dir);

        List<String> stillCutFiles = new ArrayList<>();
        try {
            for (MultipartFile file : registeredStillCut) {
                String fileName = file.getOriginalFilename();
                String filePath = uploadDir + fileName;

                // 파일을 C 드라이브의 지정된 디렉터리에 저장합니다.
                file.transferTo(new File(filePath));
                stillCutFiles.add(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "파일 저장 중 오류가 발생했습니다.");
            return "/admin/admin_layout";
        }

        movieService2.saveMovie(
                movieId, movieTitle, movieOverview, movieReleaseDate, certifications,
                posterImage, backdropImage, stillCutFiles, genres, video, runtime
        );

        // 저장 성공 메시지를 모델에 추가
        model.addAttribute("message", "영화 정보가 성공적으로 등록되었습니다.");

        return "/admin/admin_layout";
    }


}



