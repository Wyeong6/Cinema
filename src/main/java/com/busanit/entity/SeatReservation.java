package com.busanit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class SeatReservation {

    @EmbeddedId
    private SeatReservationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("scheduleId") // 복합 키 클래스의 scheduleId 필드와 매핑
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("seatId") // 복합 키 클래스의 seatId 필드와 매핑
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private boolean isReserved;

    private String reservedBy;
    private LocalDateTime reservationTime;

    public SeatReservation() {
        this.id = new SeatReservationId();
    }

    public SeatReservation(Schedule schedule, Seat seat, String reservedBy) {
        this.id = new SeatReservationId(schedule.getId(), seat.getId());
        this.schedule = schedule;
        this.seat = seat;
        this.isReserved = true;
        this.reservedBy = reservedBy;
        this.reservationTime = LocalDateTime.now();
    }

    public void updateReservedStatus(boolean isReserved) {
        this.isReserved = isReserved;
    }
}
