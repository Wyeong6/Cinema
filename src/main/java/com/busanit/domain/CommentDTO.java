package com.busanit.domain;

import com.busanit.entity.Member;
import com.busanit.entity.movie.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class CommentDTO {

    private Long cno;
    private String comment;
    private String memberId;
    private String grade;
    private Long movieId;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    public static List<CommentDTO> toDTOList(List<Comment> commentList) {
        return commentList.stream()
                .map(comment -> CommentDTO.builder()
                        .cno(comment.getCno())
                        .comment(comment.getComment())
                        .memberId(comment.getMemberId())
                        .grade(comment.getGrade())
                        .movieId(comment.getMovieId())
                        .createDate(comment.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }
}
