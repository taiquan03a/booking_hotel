package com.hotel.booking.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CircleChart {
    private Long totalRoom;
    private Long roomCart;
    private Long roomAvailable;
    private Long roomBooked;
}
