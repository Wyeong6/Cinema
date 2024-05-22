package com.busanit.repository;


import com.busanit.entity.movie.Movie;
import com.busanit.entity.movie.MovieReaction;
import com.busanit.entity.movie.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieReactionRepository extends JpaRepository<MovieReaction,Long> {
    Long countByMovieAndReactionType(Movie movie, ReactionType reactionType);
}
