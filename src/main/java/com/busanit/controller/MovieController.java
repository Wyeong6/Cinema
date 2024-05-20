package com.busanit.controller;
import com.busanit.domain.MovieDTO;
import com.busanit.entity.movie.Movie;
import com.busanit.repository.MovieRepository;
import com.busanit.service.MovieService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.IOException;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieRepository movieRepository;
    private final MovieService movieService;

    @GetMapping("/movies/Main")
    public String getDetailMovies(Model model) throws IOException {
        movieService.fetchAndStoreMoviesNowPlaying();
        movieService.fetchAndStoreMovieRuntimeAndReleaseData();
//        movieService.fetchAndStoreMovieStillCuts();

        List<MovieDTO> movies = movieService.getHotMovies();
        model.addAttribute("movies", movies);


        return "main";
    }


    // 호출 기준 순서 nowPlay - RunTimeAndReleaseData - StillCuts - Certification
    // 절대 바꾸지마세요 !!
    @GetMapping("movies/test")
    public void getMovies() throws IOException {
        movieService.fetchAndStoreMoviesNowPlaying();
        movieService.fetchAndStoreMovieRuntimeAndReleaseData();
        movieService.fetchAndStoreMovieStillCuts();
        movieService.fetchAndStoreCertificationData();
    }


    @GetMapping("/test")
    public String test(Model model) {
        List<Movie> movies = movieRepository.findAll();
        model.addAttribute("movies", movies);
        return "test";
    }
}

