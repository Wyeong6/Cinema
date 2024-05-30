package com.busanit.controller;

import com.busanit.domain.CommentDTO;
import com.busanit.domain.FormMemberDTO;
import com.busanit.domain.MemberRegFormDTO;
import com.busanit.domain.OAuth2MemberDTO;
import com.busanit.service.CommentService;
import com.busanit.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MypageController {

    private final CommentService commentService;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/")
    public String mypage(@AuthenticationPrincipal Object principal, Model model) {
        String userEmail = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            userEmail = authentication.getName(); // 현재 로그인한 사용자의 이메일
        }

        // social이 true이면 SocialMemberDTO를 사용, false이면 FormMemberDTO를 사용하는 조건문
        if(principal instanceof OAuth2MemberDTO) {
            OAuth2MemberDTO oAuth2MemberDTO = (OAuth2MemberDTO) principal;
            model.addAttribute("socialUser", "socialUser");
            // 사용자의 Id
            MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
            model.addAttribute("memberId", memberRegFormDTO.getId());

        } else if(principal instanceof FormMemberDTO) {
            FormMemberDTO formMemberDTO = (FormMemberDTO) principal;
            model.addAttribute("formUser", "formUser");
            // 사용자의 Id
            MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
            model.addAttribute("memberId", memberRegFormDTO.getId());
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
        } else if(principal instanceof FormMemberDTO) {
            FormMemberDTO formMemberDTO = (FormMemberDTO) principal;
            model.addAttribute("formUser", "formUser"); // formUser면 패스워드도 확인
        }

        MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
        model.addAttribute("member", memberRegFormDTO);

        // javascript에서 값을 사용
        try {
            String memberJson = objectMapper.writeValueAsString(memberRegFormDTO);
            model.addAttribute("memberJson", memberJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            model.addAttribute("memberJson", "{}");
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

            // 사용자 정보를 업데이트
            memberService.editMemberInfo(memberRegFormDTO);
            // 사용자의 새로운 UserDetails를 로드
            OAuth2MemberDTO oAuth2Member = (OAuth2MemberDTO) principal;
            OAuth2User updatedOAuth2User = memberService.loadOAuth2UserByUsername(memberRegFormDTO.getEmail());


            // 새로운 Authentication 객체 생성
//            OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
//                    updatedOAuth2User,
//                    updatedOAuth2User.getAuthorities(),
//                    oAuth2Member.getAuthorizedClientRegistrationId()
//            );
//            // SecurityContext에 새로운 Authentication 객체 설정
//            SecurityContextHolder.getContext().setAuthentication(newAuth);



        } else if(principal instanceof FormMemberDTO) {
            FormMemberDTO formMemberDTO = (FormMemberDTO) principal;

            // 사용자 정보를 업데이트
            memberService.editMemberInfo(memberRegFormDTO);
            // 사용자의 새로운 UserDetails를 로드
            UserDetails updatedUserDetails = memberService.loadUserByUsername(memberRegFormDTO.getEmail());
            // 새로운 Authentication 객체 생성
            Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUserDetails, null, updatedUserDetails.getAuthorities());
            // SecurityContext에 새로운 Authentication 객체 설정
            SecurityContextHolder.getContext().setAuthentication(newAuth);

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

    @PostMapping("/memberDelete")
    public String memberDelete(Long memberId) {
        memberService.memberDelete(memberId);

        return "redirect:/member/logout";
    }

}
