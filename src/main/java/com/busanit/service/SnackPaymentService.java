package com.busanit.service;

import com.busanit.domain.PointDTO;
import com.busanit.domain.SnackPaymentDTO;
import com.busanit.entity.Point;
import com.busanit.entity.SnackPayment;
import com.busanit.repository.SnackPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SnackPaymentService {
    private final SnackPaymentRepository snackPaymentRepository;

    // 결제 내역
    public Slice<SnackPaymentDTO> getSnackPaymentInfo(Long member_id, Pageable pageable) {
        Slice<SnackPayment> snackPaymentList = snackPaymentRepository.findByMember_Id(member_id, pageable);

        return SnackPaymentDTO.toDTOList(snackPaymentList);
    }
}
