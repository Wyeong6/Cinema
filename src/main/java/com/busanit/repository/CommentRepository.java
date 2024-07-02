package com.busanit.repository;

import com.busanit.entity.movie.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    //영화 ID에 대해 댓글 목록을 댓글 번호로 내림차순
    List<Comment> findByMovieMovieIdOrderByCnoDesc(Long movieId);
    //영화 ID에 대한 평균 평점을 반환합니다.
    @Query("SELECT AVG(c.grade) FROM Comment c WHERE c.movie.movieId = :movieId")
    Double findAvgRatingByMovieId(Long movieId);
    //회원 이메일과 영화 ID에 해당하는 댓글을 반환
    Optional<Comment> findCommentByMemberEmailAndMovieMovieId(String memberEmail, Long movieId);
    //회원 이메일에 해당하는 모든 댓글을 반환(리스트)
    List<Comment> findAllByMemberEmail(String memberEmail);
    //회원 이메일에 해당하는 모든 댓글을 반환(페이징)
    Page<Comment> findAll(Pageable pageable);
}
