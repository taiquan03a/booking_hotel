package com.hotel.booking.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Chart {
    private String day;
    private long price;
}
