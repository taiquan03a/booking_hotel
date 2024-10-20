package com.hotel.booking.dto.rankRoom;

import com.hotel.booking.dto.bed.BedDto;
import com.hotel.booking.model.Amenity;
import com.hotel.booking.model.Image;
import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class RankRoomResponseUser {
    private int id;
    private String name;
    private List<BedDto> bed;
    private int area;
    private List<String> image;
    private List<String> amenityList;
    private int price;
}
