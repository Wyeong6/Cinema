package com.busanit.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentSummaryDTO {

    private Long movieId;
    private List<CommentDTO> comments;
    private Double gpa; // GPA로 표기하는 것이 맞습니다.

    public CommentSummaryDTO(Long movieId, Double gpa, List<CommentDTO> comments) {
        this.movieId = movieId;
        this.gpa = gpa;
        this.comments = comments;
    }
}
