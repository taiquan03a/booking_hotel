package com.hotel.booking.dto.placeRoom;

import com.hotel.booking.dto.rankRoom.RankRoomPlaceResponse;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SearchRoomResponse {
    private int totalRoom;
    private int availableRoom;
    private int bookedRoom;
    private List<RankRoomPlaceResponse> rankList;
}
