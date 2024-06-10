package com.busanit.entity.movie;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MovieBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blackId;

    private Long movieId;

    public MovieBlacklist(Long movieId) {
        this.movieId = movieId;
    }


}
