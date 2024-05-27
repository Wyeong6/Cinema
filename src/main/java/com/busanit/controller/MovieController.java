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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService2;

    @Transactional
    @GetMapping("/movies/Main")
    public String getDetailMovies(Model model) throws IOException {

        // 메인페이지에 상영작 / 상영예정작을 오늘날짜 기준 2개월 전 / 후 로 나눌려고 추가한 날짜 변수들.
        LocalDate today = LocalDate.now();
        LocalDate twoMonthsAgo = today.minusMonths(2);
        LocalDate twoMonthsLater = today.plusMonths(2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        movieService2.fetchAndStoreMoviesNowPlaying();

        // 개봉예정작
        movieService2.fetchAndStoreMoviesUpcoming();

        movieService2.fetchAndStoreMovieRuntimeAndReleaseData();
        movieService2.fetchAndStoreMovieStillCuts();
        movieService2.fetchAndStoreCertificationData();

        //비디오가 있는 인기순영화
        List<MovieDTO> videoMovies = movieService2.getVideoMovies();
        model.addAttribute("videoMovies", videoMovies);

        //모든 영화
        List<MovieDTO> allMovies = movieService2.getAll();

        //모든 영화에서 개봉일자가 2개월 전 부터 오늘날짜 까지인거만 가져옴 즉 현재 상영작임.
        List<MovieDTO> filteredMovies = allMovies.stream()
                .filter(movie -> {
                    String releaseDateString = movie.getReleaseDate(); // Assuming getReleaseDate() returns String
                    if (releaseDateString != null && !releaseDateString.isEmpty()) {
                        LocalDate releaseDate = LocalDate.parse(releaseDateString, formatter);
                        return !releaseDate.isBefore(twoMonthsAgo) && !releaseDate.isAfter(today);
                    }
                    return false;
                }).collect(Collectors.toList());
        model.addAttribute("allMovies", filteredMovies);

        //모든 영화에서 개봉일자가 오늘날짜부터 2개월 후 까지인거만 가져옴 즉 상영예정작(개봉예정)임.
        List<MovieDTO> filteredUpcomingMovies = allMovies.stream()
                .filter(movie -> {
                    String releaseDateString = movie.getReleaseDate(); // Assuming getReleaseDate() returns String
                    if (releaseDateString != null && !releaseDateString.isEmpty()) {
                        LocalDate releaseDate = LocalDate.parse(releaseDateString, formatter);
                        return !releaseDate.isBefore(today) && !releaseDate.isAfter(twoMonthsLater);
                    }
                    return false;
                }).collect(Collectors.toList());
        model.addAttribute("upcomingMovies", filteredUpcomingMovies);


        //인기순
        List<MovieDTO> hotMovies = movieService2.getHotMovies();
        model.addAttribute("hotMovies", hotMovies);

        return "main";
    }

    //현재 상영작 페이지

    @GetMapping("/nowMovies")
    public String nowMovies(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "12") int size) {
        Page<MovieDTO> moviePage = movieService2.getMoviesPagingAndSorting(page, size);
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



//    //개봉예정 페이지
//    @GetMapping("/comingMoves")
//    public String hotMove(@RequestParam(value = "page", defaultValue = "1") int page, Model model) throws IOException {
//        Pageable pageable = PageRequest.of(page - 1, 12); // 1페이지부터 시작하도록 조정
//        Page<MovieDTO> upcomingMoviesPage = movieService2.getUpcomingMoviesPagingAndSorting(page, size);
//        model.addAttribute("moviePage", upcomingMoviesPage);
//        return "movie/movie_list_comming";
//    }

    //디테일페이지
    @GetMapping("/movies/{movieId}")
    public String movieDetailinfo(@PathVariable("movieId") Long movieId, Model model) {
        List<MovieDTO> movieInfos = movieService2.getMovieDetailInfo(movieId);
        String userEmail = movieService2.getUserEmail();

        model.addAttribute("movieInfos", movieInfos);
        model.addAttribute("movieId", movieId);
        model.addAttribute("userEmail", userEmail);

        return "movie/movie_get";
    }

    @GetMapping("/upcoming/{movieId}")
    public String upcomingDetailinfo(@PathVariable("movieId") Long movieId, Model model) {
        List<MovieDTO> movieInfos = movieService2.getMovieDetailInfo(movieId);
        String userEmail = movieService2.getUserEmail();

        model.addAttribute("movieInfos", movieInfos);
        model.addAttribute("movieId", movieId);
        model.addAttribute("userEmail", userEmail);

        return "movie/movie_get";
    }

//    // 업코밍 디테일
//    @GetMapping("/upcoming/{movieId}")
//    public String upcomingDetailinfo(@ModelAttribute Movie movie,Model model) throws IOException {
//        System.out.println("movieId = " + movie.getMovieId());
//        System.out.println("title = " + movie.getTitle());
//
//        String userEmail = movieService2.getUserEmail();
//
//        List<MovieDTO> upcomingMovies = movieService2.fetchAndStoreMoviesUpcoming();
//
//        System.out.println("업커밍 무비즈 = -----------" + upcomingMovies);
//
//        System.out.println("업커밍 디테일 인포 modelAttribute로 받아온 movie = " + movie);
//
//        MovieDTO matchingMovie = movieService2.findMovieById((movie.getMovieId()));
//
//        System.out.println("matchingMovie ===== " + matchingMovie);
//
//        for (MovieDTO movieDTO : upcomingMovies) {
//            if (movieDTO.getId().equals(movie.getMovieId()) || movieDTO.getTitle().equals(movie.getTitle())) {
//                matchingMovie = movieDTO;
//                break;
//            }
//        }
//
//        // 필터링된 영화 정보를 모델에 추가
////        if (matchingMovie != null) {
////            model.addAttribute("movieInfos", matchingMovie);
////        }
////        model.addAttribute("movieId", movie.getMovieId());
//
//        model.addAttribute("movieInfos", matchingMovie);
//        model.addAttribute("movieId", movie.getMovieId());
//        model.addAttribute("userEmail", userEmail);
//
//        return "movie/movie_get";
//    }
//

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
}



