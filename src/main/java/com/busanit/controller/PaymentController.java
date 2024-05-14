package com.busanit.controller;

import com.busanit.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/test") /*이름 수정예정*/
    public String payTest() {
        return "payment/test_pay";
    }

    @GetMapping("/complete")
    public @ResponseBody void paymentComplete(Long amount) {
        System.out.println(amount);
//        int id = UserService.getIdFromAuth();
//        paymentService.orderComplete(new PaymentDTO(amount), id);
        // DB에 저장하는 로직 넣기
    }


}
