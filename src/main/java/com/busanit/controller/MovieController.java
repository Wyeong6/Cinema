package com.busanit.controller;

import com.busanit.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/movies/test")
    public void getMovies() throws IOException {
        movieService.fetchAndStoreMoviesNowPlaying();
//        movieService.fetchAndStoreMoviesRuntime();
        movieService.fetchAndStoreMovieRuntimeAndReleaseData();
        movieService.fetchAndStoreMovieStillCuts();
    }
}
