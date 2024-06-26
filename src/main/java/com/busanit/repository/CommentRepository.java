package com.busanit.repository;

import com.busanit.entity.movie.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByMovieMovieIdOrderByCnoDesc(Long movieId);
    @Query("SELECT AVG(c.grade) FROM Comment c WHERE c.movie.movieId = :movieId")
    Double findAvgRatingByMovieId(Long movieId);

    Optional<Comment> findCommentByMemberEmailAndMovieMovieId(String memberEmail, Long movieId);

    List<Comment> findAllByMemberEmail(String memberEmail);

    Page<Comment> findAll(Pageable pageable);
}
