package com.busanit.repository;


import com.busanit.entity.movie.Movie;
import com.busanit.entity.movie.MovieReaction;
import com.busanit.entity.movie.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieReactionRepository extends JpaRepository<MovieReaction,Long> {
    Long countByMovieAndReactionType(Movie movie, ReactionType reactionType);

    Optional<MovieReaction> findByMember_EmailAndMovieMovieId(String userEmail, Long movieId);
}
