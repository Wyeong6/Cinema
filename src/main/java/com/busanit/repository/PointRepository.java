package com.busanit.repository;

import com.busanit.domain.PointDTO;
import com.busanit.entity.Point;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PointRepository extends JpaRepository<Point, Long> {

    Slice<Point> findByMember_Id(Long member_id, Pageable pageable);

    @Query("SELECT p FROM Point p WHERE p.pointType = '-' AND p.impUid = :impUid AND p.contentType = :contentType")
    Point findMinusPoint(@Param("impUid") String imp_uid, @Param("contentType") Boolean content_type);


    @Query("SELECT p FROM Point p WHERE p.pointType = '+' AND p.impUid = :impUid AND p.contentType = :contentType")
    Point findPlusPoint(@Param("impUid") String imp_uid, @Param("contentType") Boolean content_type);
}
