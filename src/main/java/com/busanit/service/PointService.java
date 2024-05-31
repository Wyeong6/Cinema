package com.busanit.service;

import com.busanit.entity.Point;
import com.busanit.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    public void savePoint(Point point) {
        pointRepository.save(point);
    }
}
