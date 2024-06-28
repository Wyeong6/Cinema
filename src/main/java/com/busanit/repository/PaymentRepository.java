package com.busanit.repository;

import com.busanit.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 결제 완료 내역 - imp_uid 로 id값 찾기
    @Query("SELECT p.id FROM Payment  p WHERE p.impUid = :impUid")
    long findByImpUid(String impUid);
}
