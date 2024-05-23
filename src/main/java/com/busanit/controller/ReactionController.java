package com.busanit.controller;

import com.busanit.entity.movie.Movie;
import com.busanit.entity.movie.ReactionType;
import com.busanit.repository.MovieRepository;
import com.busanit.service.ReactionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;
    private final MovieRepository movieRepository;


    ////// 멤버아이디 동적으로 가져와야함
    ////// 숫자 실시간으로 변하는거 해야함
    ////// 연결되는 링크 해결해야함
    @GetMapping("/movie/{movieId}")
    @ResponseBody
    public Map<String, Long> getMovieReaction(@PathVariable Long movieId) {
        Map<String, Long> reactions = new HashMap<>();
        // 반응 카운트 조회 로직
        reactions.put("likeCount", reactionService.getReactionCount(movieId, ReactionType.LIKE));
        reactions.put("dislikeCount", reactionService.getReactionCount(movieId, ReactionType.DISLIKE));
        reactions.put("sadCount", reactionService.getReactionCount(movieId, ReactionType.SAD));
        reactions.put("funnyCount", reactionService.getReactionCount(movieId, ReactionType.FUNNY));
        reactions.put("scaryCount", reactionService.getReactionCount(movieId, ReactionType.SCARY));

        // 다른 반응들도 동일하게 추가
        return reactions;
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addReaction(@RequestBody ReactionRequest reactionRequest) {
        try {
            ReactionType enumReactionType = ReactionType.valueOf(reactionRequest.getReactionType().toUpperCase());
            reactionService.addReaction(reactionRequest.getMemberId(), reactionRequest.getMovieId(), enumReactionType);
            return ResponseEntity.ok("Reaction added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid reaction type");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the reaction");
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<String> removeReaction(@RequestBody ReactionRequest reactionRequest) {

        Long memberId = reactionRequest.getMemberId();
        Long movieId = reactionRequest.getMovieId();
        String reactionType = reactionRequest.getReactionType();

        try {
            reactionService.removeReaction(memberId, movieId);
            return ResponseEntity.ok("Reaction removed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while removing the reaction");
        }
    }


    // ReactionRequest DTO 클래스
    @Setter
    @Getter
    public static class ReactionRequest {
        // Getters and setters
        private Long memberId;
        private Long movieId;
        private String reactionType;
    }

}
