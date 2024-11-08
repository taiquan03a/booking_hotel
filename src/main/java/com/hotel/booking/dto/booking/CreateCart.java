package com.hotel.booking.dto.booking;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCart {
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private Integer roomNumberId;
    private Integer adults;
    private Integer children;
    private Integer infants;
}
