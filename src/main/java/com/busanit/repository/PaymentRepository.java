package com.busanit.repository;

import com.busanit.entity.Payment;
import com.busanit.entity.SnackPayment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Slice<Payment> findByMember_IdAndProductType(Long member_id, String ProductType, Pageable pageable);

    // 결제 완료 내역 - imp_uid 로 id값 찾기
    @Query("SELECT p.id FROM Payment  p WHERE p.impUid = :impUid")
    long findByImpUid(String impUid);
}
