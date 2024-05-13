package com.busanit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    @PostMapping("/test") /*이름 수정예정*/
    public String payTest(){
        return "payment/pay";
    }

//    @PostMapping("/complete")
//    public String paymentComplete() {
//        imp_uid = extract_GET_value_from_url('imp_uid') //GET query string으로부터 imp_uid확인
//        //merchant_uid = extract_GET_value_from_url('merchant_uid') //또는, GET query string으로부터 merchant_uid확인
//
//        payment_result = rest_api_to_find_payment(imp_uid) //imp_uid로 아임포트로부터 결제정보 조회
//        amount_to_be_paid = query_amount_to_be_paid(payment_result.merchant_uid) //결제되었어야 하는 금액 조회. 가맹점에서는 merchant_uid기준으로 관리
//
//        IF payment_result.status == 'paid' AND payment_result.amount == amount_to_be_paid
//        success_post_process(payment_result) //결제까지 성공적으로 완료
//        ELSE IF payment_result.status == 'ready' AND payment.pay_method == 'vbank'
//        vbank_number_assigned(payment_result) //가상계좌 발급성공
//        ELSE
//        fail_post_process(payment_result) //결제실패 처리
//
//    }
}
