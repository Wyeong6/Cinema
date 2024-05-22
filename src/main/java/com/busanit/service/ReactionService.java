package com.busanit.service;

import com.busanit.entity.Member;
import com.busanit.entity.movie.Movie;
import com.busanit.entity.movie.ReactionType;
import com.busanit.repository.MemberRepository;
import com.busanit.repository.MovieRepository;
import com.busanit.repository.MovieReactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ReactionService {

    private final MemberRepository memberRepository;
    private final MovieRepository movieRepository;
    private final MovieReactionRepository movieReactionRepository;

    public ReactionService(MemberRepository memberRepository, MovieRepository movieRepository, MovieReactionRepository movieReactionRepository) {
        this.memberRepository = memberRepository;
        this.movieRepository = movieRepository;
        this.movieReactionRepository = movieReactionRepository;
    }

    @Transactional
    public void addReaction(Long memberId, Long movieId, ReactionType reactionType) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new IllegalArgumentException("Invalid movie ID"));
        member.addReaction(movie, reactionType);
    }

    public void removeReaction(Long memberId, Long movieId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new IllegalArgumentException("Invalid movie ID"));
        member.removeReaction(movie);
        memberRepository.save(member);
    }

    public Long getReactionCount(Long movieId, ReactionType reactionType) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new IllegalArgumentException("Invalid movie ID"));
        return movieReactionRepository.countByMovieAndReactionType(movie, reactionType);
    }

}
