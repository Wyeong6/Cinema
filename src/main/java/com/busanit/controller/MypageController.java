package com.busanit.controller;

import com.busanit.domain.*;
import com.busanit.service.CommentService;
import com.busanit.service.FavoriteMovieService;
import com.busanit.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final CommentService commentService;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final FavoriteMovieService favoriteMovieService;


    @GetMapping("/")
    public String mypage(@AuthenticationPrincipal Object principal, Model model) {
        // social이 true이면 SocialMemberDTO를 사용, false이면 FormMemberDTO를 사용하는 조건문
        if(principal instanceof OAuth2MemberDTO) {
            OAuth2MemberDTO oAuth2MemberDTO = (OAuth2MemberDTO) principal;
            model.addAttribute("socialUser", "socialUser");
        } else if(principal instanceof FormMemberDTO) {
            FormMemberDTO formMemberDTO = (FormMemberDTO) principal;
            model.addAttribute("formUser", "formUser");
        }
        return "/layout/layout_mypage";
    }

    @GetMapping("/main")
    public String mypageMain() {

        return "/mypage/mypage_main";
    }
    @GetMapping("/reservation")
    public String mypageReservation() {

        return "/mypage/mypage_reservation";
    }
    @GetMapping("/order")
    public String mypageOrder() {

        return "/mypage/mypage_order";
    }

    @GetMapping("/membership")
    public String mypageMembership() {

        return "/mypage/mypage_membership";
    }
    @GetMapping("/point")
    public String mypagePoint() {

        return "/mypage/mypage_point";
    }
    @GetMapping("/review")
    public String mypageReview(Model model) {
        String memberEmail = commentService.getAuthenticatedUserEmail();
        List<CommentDTO> comments = commentService.getAllComments(memberEmail);
        model.addAttribute("comments", comments);

        return "/mypage/mypage_review";
    }

    @GetMapping("/favorite")
    public String mypageFavorite(Model model) {
        String memberEmail = commentService.getAuthenticatedUserEmail();
        List<FavoriteMovieDTO> favoriteMovies = favoriteMovieService.getFavoriteMoviesByEmail(memberEmail);
        model.addAttribute("favoriteMovies", favoriteMovies);
        System.out.println("favoriteMovies === " + favoriteMovies.size());
        return "/mypage/mypage_favorite";
    }

    @GetMapping("/infoEdit")
    public String mypageEdit(@AuthenticationPrincipal Object principal, Model model) {
        String userEmail = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            userEmail = authentication.getName(); // 현재 로그인한 사용자의 이메일
        }

        // social이 true이면 SocialMemberDTO를 사용, false이면 FormMemberDTO를 사용
        if(principal instanceof OAuth2MemberDTO) {
            OAuth2MemberDTO oAuth2MemberDTO = (OAuth2MemberDTO) principal;
            model.addAttribute("socialUser", "socialUser");

            MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
            model.addAttribute("member", memberRegFormDTO);

        } else if(principal instanceof FormMemberDTO) {
            FormMemberDTO formMemberDTO = (FormMemberDTO) principal;
            model.addAttribute("formUser", "formUser"); // formUser면 패스워드도 확인

            MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
            model.addAttribute("member", memberRegFormDTO);
        }
        return "/mypage/mypage_private_info";
    }

    // 개인정보수정 - 이메일 중복확인
    @PostMapping("/infoEditCheckEmail")
    @ResponseBody // JSON 응답
    public Map<String, Boolean> mypageEdit1(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Long findUser = memberService.findUserIdx(email);
        boolean isAvailable = (findUser == null);

        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);

        return response;
    }

    // 개인정보수정 - 저장하기
    @PostMapping("/infoEdit")
    public String mypageEdit2(MemberRegFormDTO memberRegFormDTO, @AuthenticationPrincipal Object principal, Model model) {
        // social이 true이면 SocialMemberDTO를 사용, false이면 FormMemberDTO를 사용
        if(principal instanceof OAuth2MemberDTO) {
            OAuth2MemberDTO oAuth2MemberDTO = (OAuth2MemberDTO) principal;

        } else if(principal instanceof FormMemberDTO) {
            FormMemberDTO formMemberDTO = (FormMemberDTO) principal;

            memberService.editMemberInfo(memberRegFormDTO);

        }
        return "redirect:/mypage/";
    }

    @GetMapping("/passwordEdit")
    public String mypagePasswordEdit() {

        return "/mypage/mypage_private_password";
    }

    // mypage 비밀번호수정
    @PostMapping("/passwordEdit")
    public String mypagePasswordEdit(String basicPassword, String password, @AuthenticationPrincipal Object principal, Model model) {
        // social이 true이면 SocialMemberDTO를 사용, false이면 FormMemberDTO를 사용하는 조건문
        if(principal instanceof OAuth2MemberDTO) {
            OAuth2MemberDTO oAuth2MemberDTO = (OAuth2MemberDTO) principal;
//            // socialMemberDTO를 사용하여 처리
//            memberService.updatePassword(passwordEncoder.encode(password), oAuth2MemberDTO.getEmail());
            return "redirect:/";
        } else if(principal instanceof FormMemberDTO) {
            FormMemberDTO formMemberDTO = (FormMemberDTO) principal;
            // formMemberDTO를 사용하여 처리
            String passwordCheck = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication != null) {
                String userEmail = authentication.getName(); // 현재 로그인한 사용자의 이메일
                passwordCheck = memberService.passwordCheck(userEmail);
            }

            if(passwordEncoder.matches(basicPassword, passwordCheck)) {
                memberService.updatePassword(passwordEncoder.encode(password), formMemberDTO.getEmail());
                return "redirect:/mypage/main"; // redirect - html 파일을 반환하는게 아닌 매핑된 다른 컨트롤러 메서드를 호출
            } else {
                model.addAttribute("errorMessage", "비밀번호를 다시 확인해주세요.");
                return "mypage/mypage_private_password";
            }
        }
        return "redirect:/mypage/";
    }

}
