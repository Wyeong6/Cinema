package com.busanit.controller;

import com.busanit.domain.FormMemberDTO;
import com.busanit.domain.MemberRegFormDTO;
import com.busanit.domain.OAuth2MemberDTO;
import com.busanit.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MemberService memberService;

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
    public String mypageReview() {

        return "/mypage/mypage_review";
    }

    @GetMapping("/infoEdit")
    public String mypageEdit(@AuthenticationPrincipal Object principal, Model model) {
        String userEmail = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            userEmail = authentication.getName(); // 현재 로그인한 사용자의 이메일
        }

        // social이 true이면 SocialMemberDTO를 사용, false이면 FormMemberDTO를 사용하는 조건문
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

    @GetMapping("/passwordEdit")
    public String mypagePasswordEdit() {

        return "/mypage/mypage_private_password";
    }

}
