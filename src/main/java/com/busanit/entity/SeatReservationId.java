package com.busanit.entity;

import java.io.Serializable;
import java.util.Objects;

public class SeatReservationId implements Serializable {

    private Long scheduleId;
    private String seatId;

    public SeatReservationId() {
    }

    public SeatReservationId(Long scheduleId, String seatId) {
        this.scheduleId = scheduleId;
        this.seatId = seatId;
    }
}
