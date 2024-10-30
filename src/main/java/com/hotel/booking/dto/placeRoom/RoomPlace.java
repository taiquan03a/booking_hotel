package com.hotel.booking.dto.placeRoom;

import lombok.Data;
import java.util.List;

@Data
public class RoomPlace {
    private int roomId;
    private List<SelectRoom> listSelect;
}
