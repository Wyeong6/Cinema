package com.busanit.repository;

import com.busanit.entity.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {

    Slice<Point> findByMember_Id(Long member_id, Pageable pageable);

}
