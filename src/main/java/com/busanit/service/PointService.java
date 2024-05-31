package com.busanit.service;

import com.busanit.domain.PointDTO;
import com.busanit.entity.Point;
import com.busanit.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    public void savePoint(Point point) {
        pointRepository.save(point);
    }

    public Slice<PointDTO> getPointInfo(Long member_id, Pageable pageable) {
        Slice<Point> pointList = pointRepository.findByMember_Id(member_id, pageable);

        return PointDTO.toDTOList(pointList);
    }
}
