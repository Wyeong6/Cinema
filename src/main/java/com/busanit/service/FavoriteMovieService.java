package com.busanit.service;

import com.busanit.domain.FavoriteMovieDTO;
import com.busanit.entity.Member;
import com.busanit.entity.movie.FavoriteMovie;
import com.busanit.entity.movie.Movie;
import com.busanit.repository.FavoriteMovieRepository;
import com.busanit.repository.MemberRepository;
import com.busanit.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FavoriteMovieService {

    private final MemberRepository memberRepository;
    private final MovieRepository movieRepository;
    private final FavoriteMovieRepository favoriteMovieRepository;

    @Transactional
    public void addFavorite(FavoriteMovieDTO favoriteMovieDTO) {

        Member member = memberRepository.findByEmail(favoriteMovieDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Movie movie = movieRepository.findById(favoriteMovieDTO.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        FavoriteMovie favoriteMovie = new FavoriteMovie();
        favoriteMovie.setMember(member);
        favoriteMovie.setMovie(movie);
        favoriteMovie.setFavoritedAt(LocalDateTime.now());

        member.addFavoriteMovie(favoriteMovie);
        movie.addFavoritedBy(favoriteMovie);

        favoriteMovieRepository.save(favoriteMovie);
    }

    @Transactional
    public void removeFavorite(FavoriteMovieDTO favoriteMovieDTO) {

        Member member = memberRepository.findByEmail(favoriteMovieDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Movie movie = movieRepository.findById(favoriteMovieDTO.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        FavoriteMovie favoriteMovie = favoriteMovieRepository.findByMemberAndMovie(member, movie)
                .orElseThrow(() -> new RuntimeException("FavoriteMovie not found"));


        member.removeFavoriteMovie(favoriteMovie);
        movie.removeFavoritedBy(favoriteMovie);

        favoriteMovieRepository.delete(favoriteMovie);
    }

    public boolean checkFavoriteStatus(String userEmail, Long movieId) {
        return favoriteMovieRepository.existsByMember_EmailAndMovie_MovieId(userEmail, movieId);
    }


}
