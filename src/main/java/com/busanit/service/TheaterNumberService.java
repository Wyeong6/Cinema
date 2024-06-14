package com.busanit.service;

import com.busanit.domain.SeatDTO;
import com.busanit.domain.TheaterDTO;
import com.busanit.domain.TheaterNumberDTO;
import com.busanit.entity.Theater;
import com.busanit.entity.TheaterNumber;
import com.busanit.repository.TheaterNumberRepository;
import com.busanit.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheaterNumberService {

    @Autowired
    private TheaterNumberRepository theaterNumberRepository;

    public long getTheaterIdByTheaterNumberId(long theaterNumberId) {
        TheaterNumber theaterNumber = theaterNumberRepository.findById(theaterNumberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 theaterNumberId에 대한 TheaterNumber를 찾을 수 없습니다."));
        return theaterNumber.getTheater().getId();
    }

    public void save(TheaterNumber theaterNumber) {
        if (theaterNumber.getSeats() == null) {
            throw new IllegalArgumentException("상영관 좌석 정보가 제공되지 않았습니다.");
        }
        theaterNumberRepository.save(theaterNumber);
    }

    public TheaterNumberDTO getTheaterNumberById(Long id) {
        TheaterNumber theaterNumber = theaterNumberRepository.findById(id).orElseThrow(() -> new NullPointerException("theater null"));

        return TheaterNumberDTO.toDTO(theaterNumber);
    }

    public void deleteTheaterNumberById(Long theaterNumberId) {
        theaterNumberRepository.deleteById(theaterNumberId);
    }
}