package com.busanit.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentSummaryDTO {

    private List<CommentDTO> comments;
    private Double averageRating;

    public CommentSummaryDTO(Double averageRating, List<CommentDTO> comments) {

        this.averageRating = averageRating;
        this.comments = comments;
    }
}
