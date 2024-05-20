package com.busanit.repository;

import com.busanit.entity.movie.Comment;
import com.busanit.entity.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByMovieIdOrderByCnoDesc(Long movieId);

}
