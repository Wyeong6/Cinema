package com.busanit.service;

import com.busanit.entity.Schedule;
import com.busanit.entity.Seat;
import com.busanit.entity.SeatReservation;
import com.busanit.entity.SeatReservationId;
import com.busanit.repository.ScheduleRepository;
import com.busanit.repository.SeatRepository;
import com.busanit.repository.SeatReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.busanit.entity.Seat.generateId;

@Service
@RequiredArgsConstructor
public class SeatReservationService {
    @Autowired
    private final SeatReservationRepository seatReservationRepository;

    @Autowired
    private final ScheduleRepository scheduleRepository;

    @Autowired
    private final SeatRepository seatRepository;

    public void reserveSeat(Long scheduleId, String seatId, String reservedBy) {
        if (seatReservationRepository.existsById(new SeatReservationId(scheduleId, seatId))) {
            throw new IllegalStateException("Seat is already reserved for this schedule");
        }

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid schedule ID: " + scheduleId));
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid seat ID: " + seatId));

        SeatReservation seatReservation = new SeatReservation(schedule, seat, reservedBy);
        seatReservationRepository.save(seatReservation);
    }

    public void updateAvailableSeats(Long scheduleId, List<Seat> selectedSeats) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found for id: " + scheduleId));

        int seatsReserved = selectedSeats.size();
        schedule.decreaseAvailableSeats(seatsReserved);

        scheduleRepository.save(schedule);
    }
}