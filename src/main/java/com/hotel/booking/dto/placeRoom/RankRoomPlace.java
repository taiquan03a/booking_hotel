package com.hotel.booking.dto.placeRoom;

import lombok.Data;
import java.util.List;

@Data
public class RankRoomPlace {
    private int rankRoomId;
    private List<RoomPlace> listRoom;
}
