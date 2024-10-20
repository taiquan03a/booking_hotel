package com.hotel.booking.dto.rankRoom;

import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class EditRankRoomRequest {
    private int rankId;
    private String name;
    private List<String> bed;
    private int area;
    private List<Integer> amenityId;
    private String description;
    private List<MultipartFile> images;
}
