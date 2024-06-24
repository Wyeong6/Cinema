package com.busanit.domain;

import com.busanit.entity.Schedule;
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
    private boolean isAvailable;
    private boolean isReserved;
    private Long theaterNumberId;

    public SeatDTO() {}

    public SeatDTO(String id, Long seatRow, String seatColumn, boolean isAvailable, boolean isReserved, Long theaterNumberId) {
        TheaterNumber theaterNumber = new TheaterNumber();
        theaterNumber.setId(this.theaterNumberId);

        this.id = id;
        this.seatRow = seatRow;
        this.seatColumn = seatColumn;
        this.isAvailable = isAvailable;
        this.isReserved = isReserved;
        this.theaterNumberId = theaterNumberId;
    }

    public static SeatDTO toDTO(Seat seat) {
        return SeatDTO.builder()
                .id(seat.getId())
                .seatRow(seat.getSeatRow())
                .seatColumn(seat.getSeatColumn())
                .isAvailable(seat.isAvailable())
                .isReserved(false)
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
        seat.setAvailable(this.isAvailable());
        seat.setTheaterNumber(theaterNumber);
        return seat;
    }
}

