package com.busanit.entity.movie;

import com.busanit.domain.CommentDTO;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentSummary {

    private Long movieId;
    private List<CommentDTO> comments;
    private Double gpa; // GPA로 표기하는 것이 맞습니다.

    public CommentSummary(Long movieId, Double gpa, List<CommentDTO> comments) {
        this.movieId = movieId;
        this.gpa = gpa;
        this.comments = comments;
    }
}
