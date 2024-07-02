package com.busanit.controller;

import com.busanit.domain.movie.FavoriteMovieDTO;
import com.busanit.domain.movie.MovieDTO;
import com.busanit.service.FavoriteMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class MovieFavoriteController {

    private final FavoriteMovieService favoriteMovieService;


    @GetMapping("/check/{userEmail}/{movieId}")
    public ResponseEntity<?> checkFavoriteStatus(@PathVariable String userEmail, @PathVariable Long movieId) {
        try {
            boolean isFavorited = favoriteMovieService.checkFavoriteStatus(userEmail, movieId);
            return ResponseEntity.ok().body(Map.of("isFavorited", isFavorited));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "좋아요 상태 조회 실패"));
        }
    }

    @PostMapping("/{userEmail}/{movieId}")
    public ResponseEntity<Void> addFavorite(@PathVariable String userEmail, @PathVariable Long movieId) {
        FavoriteMovieDTO favoriteMovieDTO = new FavoriteMovieDTO();
        favoriteMovieDTO.setEmail(userEmail);
        favoriteMovieDTO.setMovieId(movieId);
        favoriteMovieService.addFavorite(favoriteMovieDTO);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{userEmail}/{movieId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable String userEmail, @PathVariable Long movieId) {
        FavoriteMovieDTO favoriteMovieDTO = new FavoriteMovieDTO();
        favoriteMovieDTO.setEmail(userEmail);
        favoriteMovieDTO.setMovieId(movieId);
        favoriteMovieService.removeFavorite(favoriteMovieDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userEmail}")
    public ResponseEntity<?> getFavoriteMovies(
            @PathVariable String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        try {
            Page<MovieDTO> favoriteMovies = favoriteMovieService.getFavoriteMovies(userEmail, page, size);
            return ResponseEntity.ok().body(Map.of(
                    "favoriteMovies", favoriteMovies.getContent(),
                    "currentPage", favoriteMovies.getNumber(),
                    "totalPages", favoriteMovies.getTotalPages()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "찜한 영화 조회 실패"));
        }
    }

}
