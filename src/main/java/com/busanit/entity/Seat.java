package com.busanit.entity;

import jakarta.persistence.*;
import lombok.Builder;
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
    private boolean isAvailable = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theaterNumber_id")
    private TheaterNumber theaterNumber;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatReservation> seatReservations = new ArrayList<>();

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
                ", isAvailable=" + isAvailable +
                ", theaterNumber=" + theaterNumber +
                '}';
    }

    public void setUnavailable() {
        isAvailable = false;
    }

    public void setAvailable() {
        isAvailable = true;
    }

    public static List<Seat> toEntity(TheaterNumber theaterNumber) {
        List<Seat> seats = new ArrayList<>();

        // theaterNumber에 속하는 좌석들만 가져와서 리스트에 추가
        List<Seat> theaterSeats = theaterNumber.getSeats();
        for (Seat seat : theaterSeats) {
            Seat newSeat = new Seat();
            newSeat.setSeatRow(seat.getSeatRow());
            newSeat.setSeatColumn(seat.getSeatColumn());
            newSeat.setAvailable(true); // 사용 가능 여부 초기화
            newSeat.setTheaterNumber(theaterNumber);
            newSeat.setId(Seat.generateId(seat.getSeatColumn(), seat.getSeatRow(), theaterNumber.getId()));
            seats.add(newSeat);
        }

        return seats;
    }
}
