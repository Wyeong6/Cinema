package com.busanit.repository;

import com.busanit.entity.movie.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByMovieIdOrderByCnoDesc(Long movieId);

//    @Query("SELECT new com.busanit.domain.CommentSummary(c.movieId, AVG(c.grade)) " +
//            "FROM Comment c " +
//            "WHERE c.movieId = :movieId " +
//            "GROUP BY c.movieId")
//    CommentSummary findCommentsAndAvgGrade(@Param("movieId") Long movieId);

    @Query("SELECT AVG(c.grade) FROM Comment c WHERE c.movie.movieId = :movieId")
    Double findAvgRatingByMovieId(Long movieId);
}
