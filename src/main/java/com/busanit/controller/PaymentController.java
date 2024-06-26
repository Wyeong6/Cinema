package com.busanit.controller;

import com.busanit.domain.*;
import com.busanit.domain.movie.MovieDTO;
import com.busanit.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final SnackService snackService;
    private final MovieService movieService;
    private final SeatService seatService;
    private final ScheduleService scheduleService;
    private final TheaterNumberService theaterNumberService;
    private final PointService pointService;
    private final MemberService memberService;

    @Value("${html5_inicis_key}")
    private String html5InicisKey;

    @GetMapping("")
    public String payment(
            @RequestParam Long scheduleId,
            @RequestParam String selectedSeats,
            @RequestParam int adultCount,
            @RequestParam int teenagerCount,
            @RequestParam int grandCount,
            @RequestParam double totalAmount,
            Model model) {
        ScheduleDTO scheduleDTO = scheduleService.getScheduleById(scheduleId);
        List<MovieDTO> movieDTOs = movieService.getMovieDetailInfo(scheduleDTO.getMovieId());

        // 현재 로그인한 사용자의 정보 (이메일, idx)
        List<String> memberInfo = new ArrayList<>();
        String userEmail = memberService.currentLoggedInEmail();
        memberInfo.add(userEmail);

        // 사용자의 등급별 적립율 + 포인트 정보
        MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
        memberInfo.add(memberRegFormDTO.getId().toString());
        long userGradeLong = memberService.userGrade();
        double gradeRate = switch ((int)userGradeLong) {
            case 1 -> 0.1;
            case 2 -> 0.05;
            default -> 0.03;
        };
        long currentPoints = 0;
        currentPoints = pointService.getCurrentPoints(memberRegFormDTO.getId());

        model.addAttribute("memberInfo", memberInfo); // 사용자 정보 리스트(이메일, idx)
        model.addAttribute("gradeInfo", gradeRate); // 사용자 등급 적립율
        model.addAttribute("pointInfo", currentPoints); // 사용자 보유 포인트
        model.addAttribute("html5InicisKey", html5InicisKey); // 결제키

        model.addAttribute("scheduleDTO", scheduleDTO);
        model.addAttribute("movieDTOs", movieDTOs);
        model.addAttribute("selectedSeats", selectedSeats);
        model.addAttribute("adultCount", adultCount);
        model.addAttribute("teenagerCount", teenagerCount);
        model.addAttribute("grandCount", grandCount);
        model.addAttribute("totalAmount", totalAmount);

        return "payment/payment_window"; // 뷰 이름 리턴
    }

    @PostMapping("/test") /*이름 수정예정*/
    public String payTest(Model model) {
        model.addAttribute("html5InicisKey", html5InicisKey);
        return "payment/test_pay";
    }

    @GetMapping("/complete")
    public @ResponseBody void paymentComplete(Long amount) {
        System.out.println(amount);
//        int id = UserService.getIdFromAuth();
//        paymentService.orderComplete(new PaymentDTO(amount), id);
        // DB에 저장하는 로직 넣기
    }

    // 스낵 cart
    @GetMapping("/cartList")
    public String cartList(Model model, @PageableDefault(size = 6) Pageable pageable) {

        // 스낵 추천 리스트(랜덤)
        Page<SnackDTO> snackDTOList = null;
        snackDTOList = snackService.getSnackListRandom(pageable);
        model.addAttribute("snackList", snackDTOList);

        // 결제
        model.addAttribute("html5InicisKey", html5InicisKey);

        // 현재 로그인한 사용자의 정보 (이메일, idx)
        List<String> memberInfo = new ArrayList<>();
        String userEmail = memberService.currentLoggedInEmail();
        memberInfo.add(userEmail);
        MemberRegFormDTO memberRegFormDTO = memberService.getFormMemberInfo(userEmail);
        memberInfo.add(memberRegFormDTO.getId().toString());
        model.addAttribute("memberInfo", memberInfo); // 사용자 정보 리스트(이메일, idx)

        return "payment/cart_list";
    }


}
