package com.busanit.repository;

import com.busanit.entity.movie.MovieDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieDetailRepository extends JpaRepository<MovieDetail, Long> {
}
