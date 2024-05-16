package com.busanit.controller;

import com.busanit.domain.MovieDTO;
import com.busanit.entity.movie.Movie;
import com.busanit.entity.movie.MovieDetail;
import com.busanit.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class MovieController {

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
}