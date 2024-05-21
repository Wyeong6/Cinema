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


    @Query(value = "SELECT c.memberId, c.comment, c.grade, (SELECT AVG(c2.grade) FROM Comment c2 WHERE c2.movie_id = c.movie_id) as avgGrade " +
            "FROM Comment c " +
            "WHERE c.movie_id = :movieId", nativeQuery = true)
    List<Object[]> findCommentsAndAvgGradeByMovieId(@Param("movieId") Long movieId);
}
