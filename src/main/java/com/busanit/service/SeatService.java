package com.busanit.service;

import com.busanit.domain.TheaterNumberDTO;
import com.busanit.entity.TheaterNumber;
import com.busanit.repository.SeatRepository;
import com.busanit.repository.TheaterNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

}