package com.hotel.booking.dto.rankRoom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRankRoomRequest {
    private String name;
    private List<String> bed;
    private int area;
    private List<Integer> amenityId;
    private String description;
    private List<MultipartFile> images;
}
