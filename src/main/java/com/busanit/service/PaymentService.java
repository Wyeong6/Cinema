package com.busanit.service;

import com.busanit.domain.PaymentDTO;
import com.busanit.entity.Payment;
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
}
