package com.busanit.controller;
import com.busanit.domain.MovieDTO;
import com.busanit.domain.MovieDetailDTO;
import com.busanit.entity.movie.Genre;
import com.busanit.entity.movie.Movie;
import com.busanit.entity.movie.MovieDetail;
import com.busanit.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String movieDetailinfo(@PathVariable("movieId") Long movieId, Model model) {
        List<MovieDTO> movieInfos = movieService2.getMovieDetailInfo(movieId);
        String userEmail = movieService2.getUserEmail();
//        List<MovieDTO> upcomingInfos = movieService2.getMovieDetailInfo(movieId);
        model.addAttribute("movieInfos", movieInfos);
        model.addAttribute("movieId", movieId);
        model.addAttribute("userEmail", userEmail);

        System.out.println("movie_get으로 넘어갈때 들고가는것들------------------------------------");
        System.out.println("movieInfos = " + movieInfos);
        System.out.println("movieId = " + movieId);
        System.out.println("userEmail = " + userEmail);
//        model.addAttribute("upcoming",)
        return "movie/movie_get";
    }

    // 업코밍 디테일
    @PostMapping("/upcoming/{movieId}")
    public String upcomingDetailinfo(@ModelAttribute Movie movie
                                     ,Model model) throws IOException {
        System.out.println("movieId = " + movie.getMovieId());
        System.out.println("title = " + movie.getTitle());



        List<MovieDTO> upcomingMovies = movieService2.fetchAndStoreUpcoming();

        MovieDTO matchingMovie = null;
        for (MovieDTO movieDTO : upcomingMovies) {
            if (movieDTO.getId().equals(movie.getMovieId())) {
                matchingMovie = movieDTO;
                break;
            }
        }

        // 필터링된 영화 정보를 모델에 추가
        if (matchingMovie != null) {
            model.addAttribute("movieInfo", matchingMovie);
        }
        model.addAttribute("movieId", movie.getMovieId());



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



