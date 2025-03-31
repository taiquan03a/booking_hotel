package com.hotel.booking.dto.serviceHotel;

import lombok.Data;

@Data
public class BookingServiceRoom {
    private int serviceRoomId;
    private int bookingId;
    private String note;
}
