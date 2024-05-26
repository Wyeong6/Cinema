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

    //댓글 작성
    @PostMapping("/new")
    public ResponseEntity<String> register(@RequestBody CommentDTO commentDTO) {
        commentService.register(commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글이 성공적으로 등록되었습니다.");
    }

    //해당 영화의 댓글리스트
    @GetMapping(value = "/movies/{movieId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentSummaryDTO> getCommentList(@PathVariable("movieId") String movieId){
        List<CommentDTO> comments = commentService.getCommentList(movieId);
        Double averageRating = commentService.getAverageRating(movieId);
        CommentSummaryDTO commentList = new CommentSummaryDTO(averageRating, comments);
        return new ResponseEntity<>(commentList, HttpStatus.OK);
    }

    @GetMapping("/movies/hasCommented/{memberEmail}/{movieId}")
    public ResponseEntity<Boolean> hasCommented(@PathVariable("memberEmail") String memberEmail, @PathVariable("movieId") Long movieId){

        System.out.println("memberEmail" + memberEmail);
        System.out.println("movieId" + movieId);

        return new ResponseEntity<>(commentService.hasCommented(memberEmail, movieId), HttpStatus.OK);
    }

    @DeleteMapping("/movies/delete/{cno}")
    public ResponseEntity<String> deleteComment(@PathVariable Long cno){
        System.out.println("commentId0" +cno);
        try{
            commentService.deleteComment(cno);
            System.out.println(" 지우기 성공");
            return ResponseEntity.ok("댓글이 삭제되었습니다.");
        }catch (Exception e){
            System.out.println("지우기 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글을 삭제하는 중 오류가 발생했습니다.");
        }
    }
}
