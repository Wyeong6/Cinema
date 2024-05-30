package com.busanit.service;

import com.busanit.domain.SeatsDTO;
import com.busanit.domain.TheaterDTO;
import com.busanit.entity.Seats;
import com.busanit.entity.Theater;
import com.busanit.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Page<TheaterDTO> getTheaterAll(Pageable pageable) {
        Page<Theater> theaters = theaterRepository.findAll(pageable);

        // Page<Entity> -> Page<DTO> 변환
        return theaters.map(entity -> {
            List<String> theaterIdx = entity.getSeats().stream()
                    .map(Seats::getTheaterIdx)
                    .toList();
            List<Long> theaterNumbers = entity.getSeats().stream()
                    .map(Seats::getTheaterNumber)
                    .collect(Collectors.toList());
            List<Long> seatsPerTheater = entity.getSeats().stream()
                    .map(Seats::getSeatsPerTheater)
                    .collect(Collectors.toList());

            return TheaterDTO.builder()
                    .id(entity.getId())
                    .theaterName(entity.getTheaterName())
                    .theaterNameEng(entity.getTheaterNameEng())
                    .region(entity.getRegion())
                    .theaterCount(entity.getTheaterCount())
                    .regDate(entity.getRegDate())
                    .updateDate(entity.getUpdateDate())
                    .theaterIdx(theaterIdx)
                    .theaterNumber(theaterNumbers)
                    .seatsPerTheater(seatsPerTheater)
                    .build();
        });
    }

    public TheaterDTO getTheaterById(Long id) {
        Theater theater = theaterRepository.findById(id).orElseThrow(() -> new NullPointerException("theater null"));

        return TheaterDTO.toDTO(theater);
    }

    public SeatsDTO getSeatsByTheaterId(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new IllegalArgumentException("상영관을 찾을 수 없습니다: " + theaterId));

        List<Long> seatsPerTheater = theater.getSeats().stream()
                .map(Seats::getSeatsPerTheater)
                .collect(Collectors.toList());

        List<Long> theaterNumbers = theater.getSeats().stream()
                .map(Seats::getTheaterNumber)
                .collect(Collectors.toList());

        return SeatsDTO.builder()
                .seatsPerTheater(seatsPerTheater)
                .theaterNumber(theaterNumbers)
                .build();
    }

    public void deleteTheaterById(Long id) {
        theaterRepository.deleteById(id);
    }
}