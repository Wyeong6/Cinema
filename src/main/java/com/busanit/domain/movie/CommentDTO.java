package com.busanit.domain.movie;

import com.busanit.entity.movie.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    private Long cno;
    private String comment;
    private String memberEmail;
    private Double grade;
    private Long movieId;
    private String movieTitle;
    private LocalDateTime createDate;


    public CommentDTO(Comment comment) {
        this.cno = comment.getCno();
        this.movieTitle = comment.getMovie().getTitle();
        this.comment = comment.getComment();
        this.grade = comment.getGrade();
        this.movieId = comment.getMovie().getMovieId();
        this.memberEmail = comment.getMember().getEmail();
        this.createDate = comment.getCreatedDate();
    }

    public static List<CommentDTO> toDTOList(List<Comment> commentList) {
        return commentList.stream()
                .map(comment -> CommentDTO.builder()
                        .cno(comment.getCno())
                        .comment(comment.getComment())
                        .memberEmail(comment.getMember().getEmail())
                        .grade(comment.getGrade())
                        .movieId(comment.getMovie().getMovieId())
                        .movieTitle(comment.getMovie().getTitle())
                        .createDate(comment.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }

    public static CommentDTO convertToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();

        commentDTO.setCno(comment.getCno());
        commentDTO.setMovieTitle(comment.getMovie().getTitle());
        commentDTO.setComment(comment.getComment());
        commentDTO.setGrade(comment.getGrade());
        commentDTO.setMovieId(comment.getMovie().getMovieId());
        commentDTO.setMemberEmail(comment.getMember().getEmail());
        commentDTO.setCreateDate(comment.getCreatedDate());

        return commentDTO;
    }

}
