package com.hotel.booking.dto.serviceHotel;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookedServiceRoom {
    private int bookingRoomId;
    private String roomName;
    private int adult;
    private int child;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private int priceService;
}
