package com.busanit.service;

import com.busanit.domain.CommentDTO;
import com.busanit.entity.Member;
import com.busanit.entity.movie.Comment;
import com.busanit.domain.CommentSummaryDTO;
import com.busanit.entity.movie.Movie;
import com.busanit.repository.CommentRepository;
import com.busanit.repository.MemberRepository;
import com.busanit.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final MovieRepository movieRepository;
    private final MemberRepository memberRepository;

    //댓글쓰기
    public void register(CommentDTO commentDTO) {


        Movie movie = movieRepository.findById(commentDTO.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 movieId: " + commentDTO.getMovieId()));
        Member member = memberRepository.findByEmail(commentDTO.getMemberEmail())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 memberId: " + commentDTO.getMemberEmail()));

        Comment comment = Comment.dtoToEntity(commentDTO,movie,member);

        comment.setMovie(movie);
        comment.setMember(member);

        member.addComment(comment);
        movie.addComment(comment);

        commentRepository.save(comment);
    }

    //댓글리스트
    public List<CommentDTO> getCommentList(String movieId) {
        List<Comment> commentList = commentRepository.findByMovieMovieIdOrderByCnoDesc(Long.valueOf(movieId));

        return CommentDTO.toDTOList(commentList);
    }

    //평균평점
    public Double getAverageRating(String movieId){
        return commentRepository.findAvgRatingByMovieId(Long.valueOf(movieId));
    }

    public Boolean hasCommented(String memberEmail , Long movieId){

        Optional<Comment> comment = commentRepository.findCommentByMemberEmailAndMovieMovieId(memberEmail, movieId);
        return comment.isPresent();

    }

    public void deleteComment(Long cno) {

        commentRepository.deleteById(cno);
    }
}
