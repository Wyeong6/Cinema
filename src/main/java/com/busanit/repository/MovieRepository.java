package com.busanit.repository;

import com.busanit.entity.movie.Movie;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    //    @EntityGraph(attributePaths = {"movieDetail", "genre", "movieImage", "movieStillCut"})
    List<Movie> findAll();
}
