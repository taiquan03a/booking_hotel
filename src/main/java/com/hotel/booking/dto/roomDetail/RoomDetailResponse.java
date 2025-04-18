package com.hotel.booking.dto.roomDetail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomDetailResponse {
    private int id;
    private Integer roomNumber;
    private String roomCode;
    private Integer capacity;
    private String location;
    private String status;
}
