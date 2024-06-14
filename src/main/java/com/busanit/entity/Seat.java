package com.busanit.entity;

import com.busanit.domain.SeatDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @PrePersist
    public void prePersist() {
        this.id = generateId(this.seatColumn, this.seatRow, this.theaterNumber.getId());
    }

    public static String generateId(String seatColumn, Long seatRow, Long theaterNumberId) {
        return theaterNumberId + seatColumn + seatRow;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id='" + id + '\'' +
                ", seatRow=" + seatRow +
                ", seatColumn='" + seatColumn + '\'' +
                ", isReserved=" + isReserved +
                ", isAvailable=" + isAvailable +
                ", theaterNumber=" + theaterNumber +
                '}';
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

    public static List<Seat> toEntity(SeatDTO seatDTO, TheaterNumber theaterNumber) {
        List<Seat> seats = new ArrayList<>();

        // 여러 좌석을 생성하여 리스트에 추가
        for (int i = 1; i <= seatDTO.getSeatRow(); i++) {
            Seat seat = new Seat();
            seat.setSeatRow((long) i);
            seat.setSeatColumn(seatDTO.getSeatColumn());
            seat.setReserved(seatDTO.isReserved());
            seat.setTheaterNumber(theaterNumber);
            seat.setId(generateId(seat.getSeatColumn(), seat.getSeatRow(), theaterNumber.getId()));

            seats.add(seat);
        }

        return seats;
    }
}
