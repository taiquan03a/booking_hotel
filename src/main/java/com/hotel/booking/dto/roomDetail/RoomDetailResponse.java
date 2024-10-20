package com.hotel.booking.dto.roomDetail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomDetailResponse {
    private Integer roomNumber;
    private String roomCode;
    private String status;
}
