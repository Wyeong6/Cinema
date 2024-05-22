package com.busanit.repository;

import com.busanit.entity.movie.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

//    List<Comment> findByMovieIdOrderByCnoDesc(Long movieId);

    List<Comment> findByMovieMovieIdOrderByCnoDesc(Long movieId);
    @Query("SELECT AVG(c.grade) FROM Comment c WHERE c.movie.movieId = :movieId")
    Double findAvgRatingByMovieId(Long movieId);
}
