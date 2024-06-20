package com.busanit.repository;

import com.busanit.entity.movie.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Movie 엔티티 만들어야함. 지금은 임시로 만들어둔것 ( 테이블 제작중!! )
    @Query("SELECT m FROM Movie m JOIN m.movieDetail md ORDER BY md.popularity DESC")
    List<Movie> findAllByOrderByMovieDetailPopularityDesc();

    @Query("SELECT m FROM Movie m JOIN m.movieDetail md WHERE md.video IS NOT NULL AND md.video <> '' ORDER BY md.popularity DESC")
    List<Movie> findByVideoTrueOrderByPopularityDesc(Pageable pageable);
    //    @EntityGraph(attributePaths = {"movieDetail", "genre", "movieImage", "movieStillCut"})

    List<Movie> findAll();

    @Query("SELECT m FROM Movie m JOIN m.movieDetail md WHERE md.releaseDate BETWEEN :startDate AND :endDate")
    Page<Movie> findAllByReleaseDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);

    List<Movie> findByTitleContaining(String title);

    List<Movie> findByModifiedTrue();
}
