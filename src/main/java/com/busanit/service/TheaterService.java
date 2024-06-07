package com.busanit.service;

import com.busanit.domain.SeatDTO;
import com.busanit.domain.TheaterNumberDTO;
import com.busanit.domain.TheaterDTO;
import com.busanit.entity.Theater;
import com.busanit.entity.TheaterNumber;
import com.busanit.repository.TheaterNumberRepository;
import com.busanit.repository.TheaterRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheaterService {

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private TheaterNumberRepository theaterNumberRepository;

    public void save(Theater theater) {
        if (theater.getTheaterNumbers() == null) {
            throw new IllegalArgumentException("상영관 좌석 정보가 제공되지 않았습니다.");
        }
        theaterRepository.save(theater);
    }

    public boolean isTheaterNameDuplicate(String theaterName) {
        return theaterRepository.existsByTheaterName(theaterName);
    }

    public boolean isTheaterNameEngDuplicate(String theaterNameEng) {
        return theaterRepository.existsByTheaterNameEng(theaterNameEng);
    }

    public Page<TheaterDTO> getTheaterAll(Pageable pageable) {
        Page<Theater> theaters = theaterRepository.findAll(pageable);

        // Page<Entity> -> Page<DTO> 변환
        return theaters.map(entity -> {
            List<TheaterNumberDTO> theaterNumbers = entity.getTheaterNumbers().stream()
                    .map(TheaterNumberDTO::toDTO)
                    .collect(Collectors.toList());

            return TheaterDTO.builder()
                    .id(entity.getId())
                    .theaterName(entity.getTheaterName())
                    .theaterNameEng(entity.getTheaterNameEng())
                    .region(entity.getRegion())
                    .theaterCount(entity.getTheaterCount())
                    .regDate(entity.getRegDate())
                    .updateDate(entity.getUpdateDate())
                    .theaterNumbers(theaterNumbers)
                    .build();
        });
    }

    public TheaterDTO getTheaterById(Long id) {
        Theater theater = theaterRepository.findById(id).orElseThrow(() -> new NullPointerException("theater null"));

        return TheaterDTO.toDTO(theater);
    }

    public List<TheaterNumberDTO> getTheaterNumbersByTheaterId(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new IllegalArgumentException("상영관을 찾을 수 없습니다: " + theaterId));

        return theater.getTheaterNumbers().stream()
                .map(TheaterNumberDTO::toDTO)
                .collect(Collectors.toList());
    }

    // 해당 지역과 상영관에 대한 좌석 정보를 포함하는 TheaterDTO를 반환하는 메서드
    public TheaterDTO getTheaterDTOWithSeats(String region, String theaterName) {
        Theater theater = theaterRepository.findByRegionAndTheaterName(region, theaterName)
                .orElseThrow(() -> new IllegalArgumentException("상영관을 찾을 수 없습니다: " + theaterName));

        if (theater == null) {
            throw new IllegalArgumentException("상영관을 찾을 수 없습니다: " + theaterName);
        }

        TheaterDTO theaterDTO = TheaterDTO.toDTO(theater);

        // TheaterDTO에 좌석 정보 추가
        List<TheaterNumberDTO> theaterNumbersWithSeats = theater.getTheaterNumbers().stream()
                .map(theaterNumber -> {
                    TheaterNumberDTO theaterNumberDTO = TheaterNumberDTO.toDTO(theaterNumber);
                    List<SeatDTO> seats = theaterNumber.getSeats().stream()
                            .map(SeatDTO::toDTO)
                            .collect(Collectors.toList());
                    theaterNumberDTO.setSeats(seats);
                    return theaterNumberDTO;
                })
                .collect(Collectors.toList());

        theaterDTO.setTheaterNumbers(theaterNumbersWithSeats);

        return theaterDTO;
    }

    public List<TheaterNumberDTO> getTheaterNumbersByTheaterName(String theaterName) {
        Theater theater = theaterRepository.findByTheaterName(theaterName)
                .orElseThrow(() -> new IllegalArgumentException("상영관을 찾을 수 없습니다: " + theaterName));

        return theater.getTheaterNumbers().stream()
                .map(TheaterNumberDTO::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteTheaterById(Long id) {
        theaterRepository.deleteById(id);
    }

    public void decreaseTheaterCountById(long theaterId) {
        theaterRepository.decreaseTheaterCountById(theaterId);
    }

    // 상영시간표
    public List<TheaterDTO> findTheatersByRegion(String region) {
        List<Theater> theaters = theaterRepository.findTheatersByRegion(region);
        return theaters.stream()
                .map(TheaterDTO::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<Theater> findByTheaterName(String theaterName) {
        return theaterRepository.findByTheaterName(theaterName);
    }
}