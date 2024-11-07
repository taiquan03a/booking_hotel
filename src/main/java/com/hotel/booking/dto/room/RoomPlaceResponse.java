package com.hotel.booking.dto.room;

import com.hotel.booking.dto.roomDetail.RoomDetailResponse;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class RoomPlaceResponse {
    private Integer roomId;
    private String roomName;
    private int adultNumber;
    private int adultMax;
    private List<RoomDetailResponse> roomNumberList;
}
