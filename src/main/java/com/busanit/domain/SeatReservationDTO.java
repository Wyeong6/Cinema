package com.busanit.domain;


import com.busanit.entity.Seat;
import com.busanit.entity.SeatReservation;
import com.busanit.entity.SeatReservationId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SeatReservationDTO {

    private SeatReservationId id;
    private Long scheduleId;
    private String seatId;
    private Boolean isReserved;
    private String reservedBy;
    private LocalDateTime reservationTime;

    public static SeatReservationDTO toDTO(SeatReservation seatReservation) {
        return SeatReservationDTO.builder()
                .id(seatReservation.getId())
                .scheduleId(seatReservation.getSchedule().getId())
                .seatId(seatReservation.getSeat().getId())
                .isReserved(seatReservation.isReserved())
                .reservedBy(seatReservation.getReservedBy())
                .reservationTime(seatReservation.getReservationTime())
                .build();
    }
}
