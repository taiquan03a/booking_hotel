package com.hotel.booking.dto.roomDetail;

import lombok.Data;

@Data
public class CreateRoomDetail {
    private int roomNumber;
    private int capacity;
    private String location;
}
