package com.busanit.service;

import com.busanit.domain.PaymentDTO;
import com.busanit.entity.Payment;
import com.busanit.entity.Point;
import com.busanit.entity.Snack;
import com.busanit.repository.MemberRepository;
import com.busanit.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;

//    public Payment orderComplete(PaymentDTO dto, int id){
//        User user = memberRepository.findUserById(id);
//        dto.setUser(user);
//        return paymentRepository.save(mapper.map(dto, Point.class))
//    }

    // 결제 DB 저장
    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    // 결제 완료 내역
    public PaymentDTO get(String imp_uid) {
        Payment payment = paymentRepository.findById(paymentRepository.findByImpUid(imp_uid)).orElseThrow(() -> new NullPointerException("payment null"));

        return PaymentDTO.toDTO(payment);
    }
}
