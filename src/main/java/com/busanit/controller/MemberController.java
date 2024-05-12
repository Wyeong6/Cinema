package com.busanit.controller;

import com.busanit.domain.MemberRegFormDTO;
import com.busanit.domain.OAuth2MemberDTO;
import com.busanit.entity.Member;
import com.busanit.service.CustomOAuth2UserDetailsService;
import com.busanit.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final CustomOAuth2UserDetailsService customOAuth2UserDetailsService;

    @GetMapping("/login")
    public String login(){
        return "member/login";
    }

    @GetMapping("/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");

        return "member/login";
    }

    @GetMapping("/new")
    public String register(Model model){
        model.addAttribute("memberRegFormDTO", new MemberRegFormDTO());

        return "member/join";
    }

    @PostMapping("/new")
    public String register(@Valid MemberRegFormDTO regFormDTO, BindingResult bindingResult, Model model){

        // 에러가 있으면 회원 가입 페이지로 이동
        if(bindingResult.hasErrors()){
            return "member/join";
        }

        try{
            memberService.saveMember(Member.createMember(regFormDTO, passwordEncoder));
        } catch(IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/join";
        }

        return "redirect:/member/login";
    }

    // 관리자 회원가입(삭제예정)
    @GetMapping("/new2")
    public String register2(Model model){
        model.addAttribute("memberRegFormDTO2", new MemberRegFormDTO());

        return "member/join2";
    }

    // 관리자 회원가입(삭제예정)
    @PostMapping("/new2")
    public String register2(@Valid MemberRegFormDTO regFormDTO, BindingResult bindingResult, Model model){

        // 에러가 있으면 회원 가입 페이지로 이동
        if(bindingResult.hasErrors()){
            return "member/join2";
        }

        try{
            memberService.saveMember(Member.createMember2(regFormDTO, passwordEncoder));
        } catch(IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/join2";
        }

        return "redirect:/member/login";
    }

    @GetMapping("/modify")
    public String modify(){
        return "member/memberModifyForm";
    }

    @PostMapping("/modify")
    public String modify(String password, @AuthenticationPrincipal OAuth2MemberDTO oAuth2MemberDTO){
        // @AuthenticationPrincipal - 현재 로그인한 사용자 객체를 파라미터(인자)에 주입할 수 있음
        customOAuth2UserDetailsService.updatePassword(passwordEncoder.encode(password),
                oAuth2MemberDTO.getEmail());
        return "redirect:/";
    }
}

