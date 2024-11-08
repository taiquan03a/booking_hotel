package com.hotel.booking.dto.roomService;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceRoomSelect {
    private int id;
    private String name;
    private int price;
    private boolean selected;
}
