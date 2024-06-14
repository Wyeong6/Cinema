package com.busanit.service;

import com.busanit.entity.Seat;
import com.busanit.domain.SeatDTO;
import com.busanit.entity.TheaterNumber;
import com.busanit.repository.SeatRepository;
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

    @Transactional
    public void save(List<SeatDTO> seatDTOList) {
        List<Seat> seats = new ArrayList<>();

        for (SeatDTO seatDTO : seatDTOList) {
            TheaterNumber theaterNumber = theaterNumberRepository.findById(seatDTO.getTheaterNumberId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid theater number ID"));

            Seat seat = new Seat();
            seat.setSeatRow(seatDTO.getSeatRow());
            seat.setSeatColumn(seatDTO.getSeatColumn());
            seat.setReserved(seatDTO.isReserved());
            seat.setTheaterNumber(theaterNumber);
            seat.setId(generateId(seat.getSeatColumn(), seat.getSeatRow(), theaterNumber.getId()));

            seats.add(seat);
        }

        seatRepository.saveAll(seats);
    }
}