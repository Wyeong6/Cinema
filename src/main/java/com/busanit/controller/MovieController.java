package com.busanit.controller;

import com.busanit.domain.MovieDTO;
import com.busanit.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


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
//    @CrossOrigin(origins = "http://localhost:3000")
//    @GetMapping("/api/movies")
//    public ResponseEntity<List<MovieDTO>> getMovies() throws IOException {
//        movieService.fetchAndStoreMoviesNowPlaying();
//        List<MovieDTO> movies = movieService.getAllMovies();
//
//        return ResponseEntity.ok(movies);
//    }
}
