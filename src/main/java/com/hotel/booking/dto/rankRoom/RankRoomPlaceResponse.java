package com.hotel.booking.dto.rankRoom;

import com.hotel.booking.dto.AmenityDto;
import com.hotel.booking.dto.room.RoomPlaceResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RankRoomPlaceResponse {
    private Integer id;
    private String name;
    private Integer area;
    private List<AmenityDto> amenity;
    private List<RoomPlaceResponse> roomPlaces;
}
