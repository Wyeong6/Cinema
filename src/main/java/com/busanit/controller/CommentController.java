package com.busanit.controller;

import com.busanit.domain.CommentDTO;
import com.busanit.domain.CommentSummaryDTO;
import com.busanit.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/new")
    public ResponseEntity<String> register(@RequestBody CommentDTO commentDTO) {
        commentService.register(commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글이 성공적으로 등록되었습니다.");
    }

    @GetMapping(value = "/movies/{movieId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentSummaryDTO> getCommentList(@PathVariable("movieId") String movieId){
        List<CommentDTO> comments = commentService.getCommentList(movieId);
        Double averageRating = commentService.getAverageRating(movieId);
        CommentSummaryDTO commentList = new CommentSummaryDTO(averageRating, comments);
        return new ResponseEntity<>(commentList, HttpStatus.OK);
    }
}
