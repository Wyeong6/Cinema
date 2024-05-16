package com.busanit.repository;

import com.busanit.entity.movie.Genre;
import com.busanit.entity.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, String> {
    Optional<Genre> findByGenreName(String genreName);
}
