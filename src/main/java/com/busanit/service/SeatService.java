package com.busanit.service;

import com.busanit.entity.Seat;
import com.busanit.domain.SeatDTO;
import com.busanit.entity.SeatReservation;
import com.busanit.entity.TheaterNumber;
import com.busanit.repository.SeatRepository;
import com.busanit.repository.SeatReservationRepository;
import com.busanit.repository.TheaterNumberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.busanit.entity.Seat.generateId;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    @Autowired
    private TheaterNumberRepository theaterNumberRepository;

    @Autowired
    private SeatReservationRepository seatReservationRepository;

    @Transactional
    public void save(List<SeatDTO> seatDTOList) {
        List<Seat> seats = new ArrayList<>();

        for (SeatDTO seatDTO : seatDTOList) {
            TheaterNumber theaterNumber = theaterNumberRepository.findById(seatDTO.getTheaterNumberId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid theater number ID"));

            Seat seat = new Seat();
            seat.setSeatRow(seatDTO.getSeatRow());
            seat.setSeatColumn(seatDTO.getSeatColumn());
            seat.setTheaterNumber(theaterNumber);
            seat.setId(generateId(seat.getSeatColumn(), seat.getSeatRow(), theaterNumber.getId()));

            seats.add(seat);
        }

        seatRepository.saveAll(seats);
    }

    public void markSeatsAsReserved(Long scheduleId, List<SeatDTO> seatDTOs) {
        // 예약 정보 조회
        List<SeatReservation> reservations = seatReservationRepository.findByScheduleIdAndSeatIdIn(
                scheduleId,
                seatDTOs.stream().map(SeatDTO::getId).collect(Collectors.toList())
        );

        // SeatDTO에 예약 여부 설정
        for (SeatDTO seat : seatDTOs) {
            boolean isReserved = reservations.stream()
                    .anyMatch(reservation -> reservation.getSeat().getId().equals(seat.getId()));
            seat.setReserved(isReserved);
        }
    }
}