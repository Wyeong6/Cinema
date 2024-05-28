package com.busanit.controller;

import com.busanit.domain.FavoriteMovieDTO;
import com.busanit.service.FavoriteMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class MovieFavoriteController {

    private final FavoriteMovieService favoriteMovieService;

    @PostMapping("/{memberId}/{movieId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long memberId, @PathVariable Long movieId) {
        FavoriteMovieDTO favoriteMovieDTO = new FavoriteMovieDTO();
        favoriteMovieDTO.setMemberId(memberId);
        favoriteMovieDTO.setMovieId(movieId);
        favoriteMovieService.addFavorite(favoriteMovieDTO);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{memberId}/{movieId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long memberId, @PathVariable Long movieId) {
        FavoriteMovieDTO favoriteMovieDTO = new FavoriteMovieDTO();
        favoriteMovieDTO.setMemberId(memberId);
        favoriteMovieDTO.setMovieId(movieId);
        favoriteMovieService.removeFavorite(favoriteMovieDTO);
        return ResponseEntity.ok().build();
    }

}
