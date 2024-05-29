package com.busanit.repository;

import com.busanit.entity.Member;
import com.busanit.entity.movie.FavoriteMovie;
import com.busanit.entity.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteMovieRepository extends JpaRepository<FavoriteMovie,Long> {

        Optional<FavoriteMovie> findByMemberAndMovie(Member member, Movie movie);

        boolean existsByMember_EmailAndMovie_MovieId(String userEmail, Long movieId);

}
