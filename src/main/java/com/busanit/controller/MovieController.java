package com.busanit.controller;
import com.busanit.domain.MovieDTO;
import com.busanit.entity.movie.Movie;
import com.busanit.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

    //현재 상영작페이지
    @GetMapping("/nowMovie")
    public String nowMovie(Model model){
        List<MovieDTO> allMovies = movieService2.getAll();
        model.addAttribute("allMovies", allMovies);

        return "movie/movie_list_now";
    }

    //개봉예정 페이지
    @GetMapping("/comingMove")
    public String hotMove(Model model) throws IOException {

        List<MovieDTO> upcomingMovies = movieService2.fetchAndStoreUpcoming();
        model.addAttribute("upcomingMovies", upcomingMovies);
        return "movie/movie_list_comming";
    }
    //디테일페이지
    @GetMapping("/movies/{movieId}")
    public String movieDetailinfo(@PathVariable("movieId") Long moviedId, Model model) {
        List<MovieDTO> movieInfos = movieService2.getMovieDetailInfo(moviedId);
        model.addAttribute("movieInfos", movieInfos);
        return "movie/movie_get";
    }

    @GetMapping("/movies/get")
    public String test(Model model) {
        return "movie/movie_get";
    }

    // 리뷰작성 모달
    @RequestMapping("/review")
    public String reviewPopup() {
        return "movie/review_modal";
    }
}



