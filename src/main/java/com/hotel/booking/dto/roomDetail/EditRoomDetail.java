package com.hotel.booking.dto.roomDetail;

import lombok.Data;

@Data
public class EditRoomDetail {
    private int roomDetailId;
    private int roomNumber;
    private int capacity;
    private String location;
}
