package com.busanit.service;

import com.busanit.domain.TheaterDTO;
import com.busanit.entity.Theater;
import com.busanit.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TheaterService {

    @Autowired
    private TheaterRepository theaterRepository;

    public void save(Theater theater) {
        if (theater.getSeats() == null) {
            throw new IllegalArgumentException("상영관 좌석 정보가 제공되지 않았습니다.");
        }
        theaterRepository.save(theater);
    }
}
