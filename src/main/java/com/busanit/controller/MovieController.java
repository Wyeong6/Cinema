package com.busanit.controller;
import com.busanit.domain.MovieDTO;
import com.busanit.entity.movie.Movie;
import com.busanit.repository.MovieRepository;
import com.busanit.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.IOException;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieRepository movieRepository;
    private final MovieService movieService2;

    @Transactional
    @GetMapping("/movies/Main")
    public String getDetailMovies(Model model) throws IOException {
        movieService2.fetchAndStoreMoviesNowPlaying();
        movieService2.fetchAndStoreMovieRuntimeAndReleaseData();
        movieService2.fetchAndStoreMovieStillCuts();
        movieService2.fetchAndStoreCertificationData();

        List<MovieDTO> videoMovies = movieService2.getVideoMovies();
        model.addAttribute("videoMovies", videoMovies);

        List<MovieDTO> allMovies = movieService2.getAll();
        model.addAttribute("allMovies", allMovies);

        List<MovieDTO> hotMovies = movieService2.getHotMovies();
        model.addAttribute("hotMovies", hotMovies);



        return "main";
    }

//    @GetMapping("/test")
//    public String test(Model model) {
//        List<Movie> movies = movieRepository.findAll();
//        model.addAttribute("movies", movies);
//        return "test";
//    }
}

