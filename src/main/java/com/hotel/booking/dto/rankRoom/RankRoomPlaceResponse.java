package com.hotel.booking.dto.rankRoom;

import com.hotel.booking.dto.AmenityDto;
import com.hotel.booking.dto.bed.BedDto;
import com.hotel.booking.dto.room.RoomPlaceResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RankRoomPlaceResponse {
    private Integer id;
    private String name;
    private List<BedDto> bed;
    private String image;
    private Integer area;
    private List<AmenityDto> amenities;
    private List<RoomPlaceResponse> roomPlaces;
}
