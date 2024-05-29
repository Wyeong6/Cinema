package com.busanit.controller;

import com.busanit.domain.FavoriteMovieDTO;
import com.busanit.service.FavoriteMovieService;
import lombok.RequiredArgsConstructor;
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
            // 예외 처리 로직. 실제 환경에서는 더 상세한 예외 처리가 필요함.
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

}
