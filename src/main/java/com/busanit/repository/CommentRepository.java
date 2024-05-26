package com.busanit.repository;

import com.busanit.entity.movie.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByMovieMovieIdOrderByCnoDesc(Long movieId);
    @Query("SELECT AVG(c.grade) FROM Comment c WHERE c.movie.movieId = :movieId")
    Double findAvgRatingByMovieId(Long movieId);

//    Optional<Comment> findByMemberEmailAndMovieMovieId(String email, Long movieId);

    Optional<Comment> findCommentByMemberEmailAndMovieMovieId(String memberEmail, Long movieId);

//    @Query("SELECT c FROM Comment c WHERE c.member.email = :memberEmail AND c.movie.id = :movieId")
//    Optional<Comment> findCommentByMemberEmailAndMovieId(@Param("memberEmail") String memberEmail, @Param("movieId") Long movieId);
}
