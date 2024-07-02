package com.busanit.repository;

import com.busanit.entity.SeatReservation;
import com.busanit.entity.SeatReservationId;
import com.busanit.entity.TheaterNumber;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {
    boolean existsById(SeatReservationId id);

    List<SeatReservation> findByScheduleIdAndSeatIdIn(Long scheduleId, List<String> seatIds);

    @Modifying
    @Query("DELETE FROM SeatReservation s WHERE s.schedule.id = :scheduleId AND s.seat.id IN :seatIds")
    void deleteByScheduleIdAndSeatId(Long scheduleId, List<String> seatIds);
}