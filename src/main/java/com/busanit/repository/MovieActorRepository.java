package com.busanit.repository;

import com.busanit.entity.movie.MovieActor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieActorRepository extends JpaRepository<MovieActor, Long> {

    List<MovieActor> findByActorNameContaining(String name);


    @Query("SELECT a.actorId FROM MovieActor a JOIN a.movies m WHERE m.movieId = :movieId")
    List<Long> findActorIdsByMovieId(String movieId);
}
