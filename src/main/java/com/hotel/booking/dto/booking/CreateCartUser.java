package com.hotel.booking.dto.booking;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCartUser {
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private Integer roomId;
    private Integer roomNumber;
}
