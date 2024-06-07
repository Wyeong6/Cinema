package com.busanit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Seat {
    @Id
    @Column(name = "seat_item_id")
    private String id;

    private Long seatRow;

    private String seatColumn;
    private boolean isReserved;
    private boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theaterNumber_id")
    private TheaterNumber theaterNumber;

    public Seat() {
    }

    public Seat(String id, Long seatRow, String seatColumn) {
        this.id = id;
        this.seatRow = seatRow;
        this.seatColumn = seatColumn;
        this.isReserved = false;
        this.isAvailable = true;
    }

    public void reserveSeat() {
        if (!isReserved && isAvailable) {
            isReserved = true;
        }
    }

    public void setUnavailable() {
        isAvailable = false;
    }

    public void setAvailable() {
        isAvailable = true;
    }

}
