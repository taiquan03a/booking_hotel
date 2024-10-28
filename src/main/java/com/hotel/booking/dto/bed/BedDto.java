package com.hotel.booking.dto.bed;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BedDto {
    private int bedId;
    private String name;
    private int quantity;
}
