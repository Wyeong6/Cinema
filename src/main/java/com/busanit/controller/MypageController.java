package com.busanit.controller;


import com.busanit.domain.movie.CommentDTO;
import com.busanit.domain.FormMemberDTO;
import com.busanit.domain.MemberRegFormDTO;
import com.busanit.domain.OAuth2MemberDTO;
import com.busanit.domain.movie.FavoriteMovieDTO;
import com.busanit.service.CommentService;
import com.busanit.service.MemberService;
import com.busanit.domain.*;
import com.busanit.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    private final FavoriteMovieService favoriteMovieService;
    private final PointService pointService;
    private final SnackPaymentService snackPaymentService;
    private final ObjectMapper objectMapper;

    @GetMapping("/")
    public String mypage(@AuthenticationPrincipal Object principal, Model model) {
        // 현재 로그인한 사용자의 이메일
        String userEmail = memberService.currentLoggedInEmail();

        // 사용자의 등급 확인+저장 (수정예정 은 아니고 추가로 다른 곳에도 넣을 예정 - pay쪽에도 넣어야함)
        long userGrade = memberService.userGrade();

        // social이 true이면 SocialMemberDTO를 사용, false이면 FormMemberDTO를 사용하는 조건문
        if(principal instanceof OAuth2MemberDTO) {
            OAuth2MemberDTO oAuth2MemberDTO = (OAuth2MemberDTO) principal;
            model.addAttribute("socialUser", "socialUser");
        } else if(principal instanceof FormMemberDTO) {
            FormMemberDTO formMemberDTO = (FormMemberDTO) principal;
            model.addAttribute("formUser", "formUser");
        }

        // 사용자의 Id
        MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
        model.addAttribute("memberId", memberRegFormDTO.getId());
        return "/layout/layout_mypage";
    }

    @GetMapping("/main")
    public String mypageMain(Model model, @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        // 현재 로그인한 사용자의 이메일
        String userEmail = memberService.currentLoggedInEmail();

        // 사용자의 정보 + 포인트 정보
        MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
        model.addAttribute("myPageMemberInfo", memberRegFormDTO);
        Slice<PointDTO> pointDTOList = null;
        pointDTOList = pointService.getPointInfo(memberRegFormDTO.getId(), pageable);
        model.addAttribute("pointInfo", pointDTOList);

        // 사용자의 등급 변환
        Integer userGradeInt = memberRegFormDTO.getGrade_code();
        String gradeString = switch (userGradeInt) {
            case 1 -> "BLACK";
            case 2 -> "RED";
            case 3 -> "BLUE";
            default -> "GREEN";
        };
        model.addAttribute("myPageGrade", gradeString);

        // 최근 스낵 주문 내역
        Slice<SnackPaymentDTO> snackPaymentDTOList = null;
        snackPaymentDTOList = snackPaymentService.getSnackPaymentInfo(memberService.findUserIdx(userEmail), pageable);
        model.addAttribute("snackPaymentInfo", snackPaymentDTOList);

        return "/mypage/mypage_main";
    }

    @GetMapping("/reservation")
    public String mypageReservation() {

        return "/mypage/mypage_reservation";
    }

    @GetMapping("/order")
    public String mypageOrder(Model model, @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        // 현재 로그인한 사용자의 이메일
        String userEmail = memberService.currentLoggedInEmail();

        // 사용자의 주문내역
        Slice<SnackPaymentDTO> snackPaymentDTOList = null;
        snackPaymentDTOList = snackPaymentService.getSnackPaymentInfo(memberService.findUserIdx(userEmail), pageable);
        model.addAttribute("snackPaymentInfo", snackPaymentDTOList);

        return "/mypage/mypage_order";
    }

    @GetMapping("/order/more")
    @ResponseBody
    public Slice<SnackPaymentDTO> getOrders(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (authentication != null) ? authentication.getName() : null;
        MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
        return snackPaymentService.getSnackPaymentInfo(memberRegFormDTO.getId(), pageable);
    }

    @GetMapping("/order/detail")
    public String mypageOrderDetail() {
        return "/mypage/mypage_order_detail";
    }

    @GetMapping("/membership")
    public String mypageMembership(Model model) {
        // 현재 로그인한 사용자의 이메일
        String userEmail = memberService.currentLoggedInEmail();

        // 사용자의 정보
        MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
        model.addAttribute("myPageMemberInfo", memberRegFormDTO);

        // 사용자의 등급 변환
        Integer userGradeInt = memberRegFormDTO.getGrade_code();
        String gradeString = switch (userGradeInt) {
            case 1 -> "BLACK";
            case 2 -> "RED";
            case 3 -> "BLUE";
            default -> "GREEN";
        };
        model.addAttribute("myPageGrade", gradeString);

        return "/mypage/mypage_membership";
    }

    @GetMapping("/point")
    public String mypagePoint(Model model, @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        // 현재 로그인한 사용자의 이메일
        String userEmail = memberService.currentLoggedInEmail();

        // 사용자의 정보 + 포인트 정보
        MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
        model.addAttribute("myPageMemberInfo", memberRegFormDTO);
        Slice<PointDTO> pointDTOList = null;
        pointDTOList = pointService.getPointInfo(memberRegFormDTO.getId(), pageable);
        model.addAttribute("pointInfo", pointDTOList);

        return "/mypage/mypage_point";
    }

    @GetMapping("/point/more")
    @ResponseBody
    public Slice<PointDTO> getPoints(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (authentication != null) ? authentication.getName() : null;
        MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
        return pointService.getPointInfo(memberRegFormDTO.getId(), pageable);
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
        model.addAttribute("hasFavorites", !favoriteMovies.isEmpty());
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
        } else if(principal instanceof FormMemberDTO) {
            FormMemberDTO formMemberDTO = (FormMemberDTO) principal;
            model.addAttribute("formUser", "formUser"); // formUser면 패스워드도 확인
        }

        MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
        model.addAttribute("member", memberRegFormDTO);

        // javascript에서 값을 사용
        try {
            String memberJson = objectMapper.writeValueAsString(memberRegFormDTO);
            model.addAttribute("memberJsonModel", memberJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            model.addAttribute("memberJsonModel", "{}");
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
    public String mypageEdit2(@RequestParam(name = "basicPassword", required = false) String basicPassword, MemberRegFormDTO memberRegFormDTO, @AuthenticationPrincipal Object principal, Model model, RedirectAttributes redirectAttributes) {
        // social이 true이면 SocialMemberDTO를 사용, false이면 FormMemberDTO를 사용
        if(principal instanceof OAuth2MemberDTO) {
            OAuth2MemberDTO oAuth2MemberDTO = (OAuth2MemberDTO) principal;

            // 사용자 정보를 업데이트
            memberService.editMemberInfo(memberRegFormDTO);
            // 사용자의 새로운 UserDetails를 로드
            OAuth2MemberDTO oAuth2Member = (OAuth2MemberDTO) principal;
            OAuth2User updatedOAuth2User = memberService.loadOAuth2UserByUsername(memberRegFormDTO.getEmail());
            // 새로운 Authentication 객체 생성
            OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
                    updatedOAuth2User,
                    updatedOAuth2User.getAuthorities(),
                    oAuth2Member.getAuthorizedClientRegistrationId() != null ? oAuth2MemberDTO.getAuthorizedClientRegistrationId() : "social" // 클라이언트 등록 ID 사용
            );
            // SecurityContext에 새로운 Authentication 객체 설정
            SecurityContextHolder.getContext().setAuthentication(newAuth);

        } else if(principal instanceof FormMemberDTO) {
            FormMemberDTO formMemberDTO = (FormMemberDTO) principal;

            // 비밀번호 확인
            String passwordCheck = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication != null) {
                String userEmail = authentication.getName(); // 현재 로그인한 사용자의 이메일
                passwordCheck = memberService.passwordCheck(userEmail);
            }

            if(passwordEncoder.matches(basicPassword, passwordCheck)) { // 비밀번호 일치
                // 사용자 정보를 업데이트
                memberService.editMemberInfo(memberRegFormDTO);
                // 사용자의 새로운 UserDetails를 로드
                UserDetails updatedUserDetails = memberService.loadUserByUsername(memberRegFormDTO.getEmail());
                // 새로운 Authentication 객체 생성
                Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUserDetails, null, updatedUserDetails.getAuthorities());
                // SecurityContext에 새로운 Authentication 객체 설정
                SecurityContextHolder.getContext().setAuthentication(newAuth);
                return "redirect:/mypage/main"; // redirect - html 파일을 반환하는게 아닌 매핑된 다른 컨트롤러 메서드를 호출

            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "비밀번호를 다시 확인해주세요.");
                return "redirect:/mypage/infoEdit";
            }
        }
        return "redirect:/mypage/main";
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
