package com.busanit.controller;

import com.busanit.domain.MovieDTO;
import com.busanit.entity.movie.Movie;
import com.busanit.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService2;

    @Transactional
    @GetMapping("/movies/Main")
    public String getDetailMovies(Model model) throws IOException {
        movieService2.fetchAndStoreMoviesNowPlaying();
        movieService2.fetchAndStoreMovieRuntimeAndReleaseData();
        movieService2.fetchAndStoreMovieStillCuts();
        movieService2.fetchAndStoreCertificationData();

        //비디오가 있는 인기순영화
        List<MovieDTO> videoMovies = movieService2.getVideoMovies();
        model.addAttribute("videoMovies", videoMovies);

        //모든 영화
        List<MovieDTO> allMovies = movieService2.getAll();
        model.addAttribute("allMovies", allMovies);

        //인기순
        List<MovieDTO> hotMovies = movieService2.getHotMovies();
        model.addAttribute("hotMovies", hotMovies);

        //개봉예정
        List<MovieDTO> upcomingMovies = movieService2.fetchAndStoreUpcoming();
        model.addAttribute("upcomingMovies", upcomingMovies);

        return "main";
    }

    //현재 상영작 페이지
    @GetMapping("/nowMovies")
    public String nowMovies(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieDTO> moviePage = movieService2.getAll2(pageable);
        model.addAttribute("moviePage", moviePage);

        return "movie/movie_list_now";
    }

    //현재 상영작 페이지 페이지네이션
    @GetMapping("/nowMoviesPaging")
    public String nowMoviesPage(@RequestParam("page") int page, Model model) {
        // 페이지 번호에 따른 영화 목록을 가져오는 로직
        Page<MovieDTO> moviePage = movieService2.getMoviespaging(page);
        model.addAttribute("moviePage", moviePage);
        return "movie/movie_list_now";
    }

    //개봉예정 페이지
    @GetMapping("/comingMoves")
    public String hotMove(@RequestParam(value = "page", defaultValue = "1") int page, Model model) throws IOException {
        Pageable pageable = PageRequest.of(page - 1, 12); // 1페이지부터 시작하도록 조정
        Page<MovieDTO> upcomingMoviesPage = movieService2.getUpcomingMovies(pageable);
        model.addAttribute("moviePage", upcomingMoviesPage);
        return "movie/movie_list_comming";
    }

    //    개봉예정 페이지 페이지네이션
    @GetMapping("/commingMoviesPaging")
    public String listMovies(@RequestParam("page") int page, Model model) {
        // 페이지 번호에 따른 영화 목록을 가져오는 로직
        Page<MovieDTO> moviePage = movieService2.getMoviespaging(page);
        model.addAttribute("moviePage", moviePage);
        return "movie/movie_list_comming";
    }

    //디테일페이지
    @GetMapping("/movies/{movieId}")
    public String movieDetailinfo(@PathVariable("movieId") Long movieId, Model model) {
        List<MovieDTO> movieInfos = movieService2.getMovieDetailInfo(movieId);
        model.addAttribute("movieInfos", movieInfos);
        model.addAttribute("movieId", movieId);
        return "movie/movie_get";
    }

    @GetMapping("/movies/get")
    public String test(Model model) {
        return "movie/movie_get";
    }

    // 리뷰작성 모달
    @RequestMapping("/review/{movieId}")
    public String reviewPopup(@PathVariable("movieId") String movieId, Model model) {
        System.out.println("무비컨트롤의 무비아이디ㅁㄴㅇㄹㄴㅁㄹㅇㅁㄴㄹ" + movieId);
        model.addAttribute("movieId", movieId);
        return "movie/review_modal";
    }
}



