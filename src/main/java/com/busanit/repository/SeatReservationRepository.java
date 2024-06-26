package com.busanit.repository;

import com.busanit.entity.SeatReservation;
import com.busanit.entity.SeatReservationId;
import com.busanit.entity.TheaterNumber;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {
    boolean existsById(SeatReservationId id);

    List<SeatReservation> findByScheduleIdAndSeatIdIn(Long scheduleId, List<String> seatIds);
}