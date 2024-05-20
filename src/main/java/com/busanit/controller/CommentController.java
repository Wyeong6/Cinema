package com.busanit.controller;

import com.busanit.domain.CommentDTO;
import com.busanit.entity.Member;
import com.busanit.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

//    @PostMapping("/new")
//    //@ResponseStatus(HttpStatus.CREATED) - 상태 코드만 반환할 경우
//    public String register(@ModelAttribute CommentDTO commentDTO){
//        commentService.register(commentDTO);
//        //메세지를 날리고 싶은 경우
//        return "redirect:/list"; // 댓글 등록 성공 후 리스트 페이지로 리다이렉트
//    }
//
//    @GetMapping("/list")
//    public String getCommentList(Model model){
//        List<CommentDTO> commentList = commentService.getCommentList();
//        model.addAttribute("comments", commentList);
//        return "movie/commentsList"; // 댓글 목록을 보여주는 HTML 뷰의 이름
//    }
//    @GetMapping("/new")
//    public String getCommentForm() {
//        return "movie/comment"; // commentForm.html을 반환
//    }


    //    ----------------------------restController 쓸때
    @PostMapping("/new")
    public ResponseEntity<String> register(@RequestBody CommentDTO commentDTO) {
        commentService.register(commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글이 성공적으로 등록되었습니다.");
    }


    @GetMapping(value = "/movies/{movieId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CommentDTO>> getCommentList(@PathVariable("movieId") String movieId){
        List<CommentDTO> commentList = commentService.getCommentList(movieId);

        return new ResponseEntity<>(commentList, HttpStatus.OK);
    }
}
