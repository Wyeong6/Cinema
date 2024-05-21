package com.busanit.controller;

import com.busanit.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${html5_inicis_key}")
    private String html5InicisKey;

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
    public String cartList(Model model) {
        model.addAttribute("html5InicisKey", html5InicisKey);
        return "payment/cart_list";
    }


}
