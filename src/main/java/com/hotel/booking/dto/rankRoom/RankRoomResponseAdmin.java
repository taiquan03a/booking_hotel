package com.hotel.booking.dto.rankRoom;

import com.hotel.booking.dto.AmenityDto;
import com.hotel.booking.dto.bed.BedDto;
import com.hotel.booking.model.Amenity;
import com.hotel.booking.model.Image;
import com.hotel.booking.model.RoomBed;
import lombok.Data;

import java.util.*;

@Data
public class RankRoomResponseAdmin {
    private Integer id;
    private String name;
    private Integer area;
    private List<AmenityDto> amenity;
    private String description;
    private Boolean active;
    private List<String> images;
    private List<BedDto> bed;
}
