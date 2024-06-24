package com.busanit.controller;

import com.busanit.domain.ScheduleDTO;
import com.busanit.domain.SeatDTO;
import com.busanit.domain.SnackDTO;
import com.busanit.domain.TheaterNumberDTO;
import com.busanit.domain.movie.MovieDTO;
import com.busanit.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        return "payment/cart_list";
    }


}
