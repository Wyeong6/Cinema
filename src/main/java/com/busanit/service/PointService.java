package com.busanit.service;

import com.busanit.domain.PointDTO;
import com.busanit.entity.Point;
import com.busanit.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    // 포인트 적립
    public void savePoint(Point point) {
        pointRepository.save(point);
    }

    // 포인트 내역
    public Slice<PointDTO> getPointInfo(Long member_id, Pageable pageable) {
        Slice<Point> pointList = pointRepository.findByMember_Id(member_id, pageable);

        return PointDTO.toDTOList(pointList);
    }

    // 최근 3개월간 영화관람 count
    public long getPointMovieCount(Long memberId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);
        return pointRepository.countByMovieMembership(memberId, startDate, endDate);
    }
}
