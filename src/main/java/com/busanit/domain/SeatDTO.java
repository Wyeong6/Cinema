package com.busanit.domain;

import com.busanit.entity.Seat;
import com.busanit.entity.TheaterNumber;
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
    private boolean isReserved;
    private boolean isAvailable;
    private Long theaterNumberId;

    public SeatDTO() {}

    public SeatDTO(String id, Long seatRow, String seatColumn, boolean isReserved, boolean isAvailable, Long theaterNumberId) {
        TheaterNumber theaterNumber = new TheaterNumber();
        theaterNumber.setId(this.theaterNumberId);

        this.id = id;
        this.seatRow = seatRow;
        this.seatColumn = seatColumn;
        this.isReserved = isReserved;
        this.isAvailable = isAvailable;
        this.theaterNumberId = theaterNumberId;
    }

    public static SeatDTO toDTO(Seat seat) {
        return SeatDTO.builder()
                .id(seat.getId())
                .seatRow(seat.getSeatRow())
                .seatColumn(seat.getSeatColumn())
                .isReserved(seat.isReserved())
                .isAvailable(seat.isAvailable())
                .theaterNumberId(seat.getTheaterNumber().getId())
                .build();
    }

    public Seat toEntity() {
        TheaterNumber theaterNumber = new TheaterNumber();
        theaterNumber.setId(this.theaterNumberId);

        Seat seat = new Seat();
        seat.setId(this.id);
        seat.setSeatColumn(this.seatColumn);
        seat.setSeatRow(this.seatRow);
        seat.setReserved(this.isReserved());
        seat.setAvailable(this.isAvailable());
        seat.setTheaterNumber(theaterNumber);
        return seat;
    }
}

