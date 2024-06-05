package com.busanit.service;

import com.busanit.domain.movie.FavoriteMovieDTO;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
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

    public List<FavoriteMovieDTO> getFavoriteMoviesByEmail(String email) {
        List<FavoriteMovie> favoriteMovies = favoriteMovieRepository.findByMember_Email(email);
        return favoriteMovies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private FavoriteMovieDTO convertToDto(FavoriteMovie favoriteMovie) {
        FavoriteMovieDTO favoriteMovieDTO = new FavoriteMovieDTO();
        favoriteMovieDTO.setFavoriteId(favoriteMovie.getFavoriteId());
        favoriteMovieDTO.setEmail(favoriteMovie.getMember().getEmail()); // 가정: FavoriteMovie 엔티티에 Member 엔티티가 연결되어 있고, 이메일을 가져올 수 있음
        favoriteMovieDTO.setMovieId(favoriteMovie.getMovie().getMovieId()); // 가정: FavoriteMovie 엔티티에 Movie 엔티티가 연결되어 있고, ID를 가져올 수 있음
        favoriteMovieDTO.setMovieTitle(favoriteMovie.getMovie().getTitle());
        favoriteMovieDTO.setMoviePosterUrl(favoriteMovie.getMovie().getImages().get(0).getPosterPath());
        favoriteMovieDTO.setFavoritedAt(favoriteMovie.getFavoritedAt());
        return favoriteMovieDTO;
    }

}
