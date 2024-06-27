package com.busanit.service;

import com.busanit.domain.movie.CommentDTO;
import com.busanit.entity.Member;
import com.busanit.entity.movie.Comment;
import com.busanit.entity.movie.Movie;
import com.busanit.repository.CommentRepository;
import com.busanit.repository.MemberRepository;
import com.busanit.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseGet(() -> {
                    Movie newMovie = new Movie();
                    newMovie.setMovieId(commentDTO.getMovieId());
                    return newMovie;
                });

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
    //마이페이지의 리뷰리스트
    public List<CommentDTO> getAllComments(String memberEmail){
        List<Comment> commentList = commentRepository.findAllByMemberEmail(memberEmail);

        return CommentDTO.toDTOList(commentList);
    }

    public List<CommentDTO> getAllComment() {
        List<Comment> commentList = commentRepository.findAll();

        return CommentDTO.toDTOList(commentList);

    }

    //평균평점
    public Double getAverageRating(String movieId){
        return commentRepository.findAvgRatingByMovieId(Long.valueOf(movieId));
    }
    //회원이 등록한 댓글여부 확인
    public Boolean hasCommented(String memberEmail , Long movieId){

        Optional<Comment> comment = commentRepository.findCommentByMemberEmailAndMovieMovieId(memberEmail, movieId);
        return comment.isPresent();

    }
    //로그인한 유저 검사
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
    }
    //로그인한 유저의 이메일을 리턴
    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            return authentication.getName();
        }
        return null;
    }
    //댓글 삭제
    public void deleteComment(Long cno) {
        commentRepository.deleteById(cno);
    }



}
