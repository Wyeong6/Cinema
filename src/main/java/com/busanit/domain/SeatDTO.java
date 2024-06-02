package com.busanit.domain;

import com.busanit.entity.Seat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SeatDTO {
    private String id;
    private Long seatRow;
    private String seatColumn;
    private boolean reserved;
    private boolean available;
    private String unavailableReason;

    public static SeatDTO toDTO(Seat seat) {
        return SeatDTO.builder()
                .id(seat.getId())
                .seatRow(seat.getSeatRow())
                .seatColumn(seat.getSeatColumn())
                .reserved(seat.isReserved())
                .available(seat.isAvailable())
                .unavailableReason(seat.getUnavailableReason())
                .build();
    }
}

