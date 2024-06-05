package com.busanit.controller;


import com.busanit.domain.movie.MovieDTO;
import com.busanit.domain.SnackDTO;
import com.busanit.service.MovieService;
import com.busanit.service.SnackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService2;
    private final SnackService snackService;

    @Transactional
    @GetMapping("/")
    public String getDetailMovies(Model model, @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

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

        //스낵스토어
        Page<SnackDTO> snackDTOList = null;
        snackDTOList = snackService.getSnackList(pageable);
        model.addAttribute("snackList", snackDTOList);

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


    @GetMapping("/movies/get")
    public String test(Model model) {
        return "movie/movie_get";
    }

    // 리뷰작성 모달
    @RequestMapping("/review/{movieId}")
    public String reviewPopup(@PathVariable("movieId") String movieId, Model model) {
        String userEmail = movieService2.getUserEmail();

        model.addAttribute("userEmail", userEmail);
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

}



