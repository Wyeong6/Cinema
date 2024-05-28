package com.busanit.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteMovieDTO {
        private Long favoriteId;
        private Long memberId;
        private Long movieId;
        private LocalDateTime favoritedAt;
}
