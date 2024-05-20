package com.busanit.controller;

import com.busanit.domain.FormMemberDTO;
import com.busanit.domain.MemberRegFormDTO;
import com.busanit.domain.OAuth2MemberDTO;
import com.busanit.entity.Member;
import com.busanit.service.CustomOAuth2UserDetailsService;
import com.busanit.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    //    private final TermsService termsService;
    private final CustomOAuth2UserDetailsService customOAuth2UserDetailsService;

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @GetMapping("/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");

        return "member/login";
    }

    @GetMapping("/new")
    public String register(Model model) {
        model.addAttribute("memberRegFormDTO", new MemberRegFormDTO());

        return "member/join";
    }

    @PostMapping("/new")
//    public String register(@Valid MemberRegFormDTO regFormDTO, TermsAgreeDTO termsAgreeDTO, BindingResult bindingResult, Model model){
    public String register(@Valid MemberRegFormDTO regFormDTO, BindingResult bindingResult, Model model) {
        // 에러가 있으면 회원 가입 페이지로 이동
        if(bindingResult.hasErrors()) {
            return "member/join";
        }
        try {
            memberService.saveMember(Member.createMember(regFormDTO, passwordEncoder));
//            termsService.saveTerms(termsService.createTermsAgree(regFormDTO, termsAgreeDTO));
        } catch(IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/join";
        }

        return "redirect:/member/login";
    }

    // 관리자 회원가입(삭제예정)
    @GetMapping("/new2")
    public String register2(Model model) {
        model.addAttribute("memberRegFormDTO2", new MemberRegFormDTO());

        return "member/join2";
    }

    // 관리자 회원가입(삭제예정)
    @PostMapping("/new2")
    public String register2(@Valid MemberRegFormDTO regFormDTO, BindingResult bindingResult, Model model) {
        // 에러가 있으면 회원 가입 페이지로 이동
        if(bindingResult.hasErrors()) {
            return "member/join2";
        }
        try {
            memberService.saveMember(Member.createMember2(regFormDTO, passwordEncoder));
        } catch(IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/join2";
        }

        return "redirect:/member/login";
    }

    // 아이디(이메일) 찾기
    @GetMapping("/findId")
    public String findId() {
        return "member/findId";
    }

    @PostMapping("/findId")
    public String findId(String name, String age, Model model) {
        List<String> beforeMasking = memberService.findUserEmails(name, age);
        model.addAttribute("findInfo", memberService.maskingEmails(beforeMasking));

        return "member/findResult";
    }

    // 패스워드 찾기
    @GetMapping("/findPassword")
    public String findPassword() {
        return "member/findPassword";
    }

    @PostMapping("/findPassword")
    public String findPassword(String name, String age, String email, Model model) {
        boolean findPassword = memberService.findUserPassword(name, age, email);

        if(findPassword) { // 해당 정보값을 가진 사용자가 존재
            String newPwd = RandomStringUtils.randomAlphanumeric(10);
            memberService.updatePassword(passwordEncoder.encode(newPwd), email);
            model.addAttribute("findInfo", newPwd);
            model.addAttribute("newPassword", "true");
        } else { // 해당 정보값을 가진 사용자가 존재하지 않음
            model.addAttribute("findInfo", null);
        }

        return "member/findResult";
    }

    // password 수정
    @GetMapping("/modify")
    public String modifyPassword() {
        return "member/memberPasswordModify";
    }

    @PostMapping("/modify")
    public String modifyPassword(String password, @AuthenticationPrincipal Object principal) {
        // social이 true이면 SocialMemberDTO를 사용, false이면 FormMemberDTO를 사용하는 조건문
        if(principal instanceof OAuth2MemberDTO) {
            OAuth2MemberDTO oAuth2MemberDTO = (OAuth2MemberDTO) principal;
            // socialMemberDTO를 사용하여 처리
            memberService.updatePassword(passwordEncoder.encode(password), oAuth2MemberDTO.getEmail());
        } else if(principal instanceof FormMemberDTO) {
            FormMemberDTO formMemberDTO = (FormMemberDTO) principal;
            // formMemberDTO를 사용하여 처리
            memberService.updatePassword(passwordEncoder.encode(password), formMemberDTO.getEmail());
        }
        return "redirect:/";
    }

    // 소셜 로그인 특정 조건(비밀번호 재설정x or 나이 재설정x)일때 뜨는 페이지
    @GetMapping("/modifySocialInfo")
    public String modifySocialInfo() {
        return "member/memberSocialInfoModify";
    }

    @PostMapping("/modifySocialInfo")
    public String modifySocialInfo(String password, String age, @AuthenticationPrincipal OAuth2MemberDTO oAuth2MemberDTO) {
        // @AuthenticationPrincipal - 현재 로그인한 사용자 객체를 파라미터(인자)에 주입할 수 있음
        customOAuth2UserDetailsService.updatePasswordAndAge(passwordEncoder.encode(password), age,
                oAuth2MemberDTO.getEmail());
        return "redirect:/";
    }
}

