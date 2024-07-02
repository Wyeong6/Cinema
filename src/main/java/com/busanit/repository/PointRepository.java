package com.busanit.repository;

import com.busanit.entity.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PointRepository extends JpaRepository<Point, Long> {

    Slice<Point> findByMember_Id(Long member_id, Pageable pageable);

    @Query("SELECT p.points FROM Point p WHERE p.pointType = '-' AND p.impUid = :impUid")
    String findMinusPoint(@Param("impUid") String imp_uid);

    @Query("SELECT p.points FROM Point p WHERE p.pointType = '+' AND p.impUid = :impUid")
    String findPlusPoint(@Param("impUid") String imp_uid);

//    @Query("SELECT COUNT(p) FROM Point p WHERE p.member.id = :memberId AND p.contentType = true AND p.regDate >= :startDate AND p.regDate <= :endDate")
//    long countByMovieMembership(
//            @Param("memberId") Long memberId,
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate
//    );

//    // 결제시 사용 가능 포인트
//    @Query("SELECT p.currentPoints FROM Point p WHERE p.member.id = :memberId")
//    long currentPoints(@Param("memberId") Long memberId);
}
