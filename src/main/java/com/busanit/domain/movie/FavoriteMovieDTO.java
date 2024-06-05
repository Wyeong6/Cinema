package com.busanit.domain.movie;

import com.busanit.entity.movie.FavoriteMovie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class FavoriteMovieDTO {
        private Long favoriteId;
        private String email;
        private Long movieId;
        private LocalDateTime favoritedAt;
        private String movieTitle;
        private String moviePosterUrl;

        public FavoriteMovieDTO(FavoriteMovie favoriteMovie) {
                this.favoriteId = favoriteMovie.getFavoriteId();
                this.email = favoriteMovie.getMember().getEmail();
                this.movieId = favoriteMovie.getMovie().getMovieId();
                this.movieTitle = favoriteMovie.getMovie().getTitle();
                this.moviePosterUrl = favoriteMovie.getMovie().getImages().get(0).getPosterPath();
                this.favoritedAt = favoriteMovie.getFavoritedAt();
        }

        public FavoriteMovieDTO(FavoriteMovieDTO favoriteMovieDTO) {
                this.favoriteId = favoriteMovieDTO.getFavoriteId();
                this.email = favoriteMovieDTO.getEmail();
                this.movieId = favoriteMovieDTO.getMovieId();
                this.movieTitle = favoriteMovieDTO.getMovieTitle();
                this.moviePosterUrl = favoriteMovieDTO.getMoviePosterUrl();
                this.favoritedAt = favoriteMovieDTO.getFavoritedAt();
                System.out.println("movieTitle===" + movieTitle);
                System.out.println("moviePosterUrl===" + moviePosterUrl);
        }
}
