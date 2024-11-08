package com.hotel.booking.dto.booking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingRoomResponse {
    private int roomNumber;
    private String roomCode;
    private String roomType;
    private String roomName;
    private String checkin;
    private String checkout;
    private int price;
}
