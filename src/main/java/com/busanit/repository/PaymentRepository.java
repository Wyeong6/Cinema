package com.busanit.repository;

import com.busanit.entity.Payment;
import com.busanit.entity.SnackPayment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Slice<Payment> findByMember_IdAndProductTypeContaining(Long member_id, String ProductType, Pageable pageable);

    // 결제 완료 내역 - imp_uid 로 id값 찾기
    @Query("SELECT p.id FROM Payment  p WHERE p.impUid = :impUid")
    long findByImpUid(String impUid);

    // 회원 등급 조회
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.member.id = :memberId AND p.productType = 'MO' AND p.paymentStatus = '결제완료' AND p.regDate >= :startDate AND p.regDate <= :endDate")
    long countByMovieMembership(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 결제 취소 시 결제 상태 수정
    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.paymentStatus = '결제취소' WHERE p.impUid = :impUid AND p.member.id = :memberId")
    void updatePaymentStatus(String impUid, Long memberId);
}
