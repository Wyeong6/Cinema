package com.busanit.repository;

import com.busanit.entity.movie.Movie;
import com.busanit.entity.movie.MovieStillCut;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieStillCutRepository extends JpaRepository<MovieStillCut, Long> {
    boolean existsByMovies_movieIdAndStillCuts(Long movieId, String filePath);
}
