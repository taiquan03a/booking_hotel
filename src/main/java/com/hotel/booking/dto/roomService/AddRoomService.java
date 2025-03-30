package com.hotel.booking.dto.roomService;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddRoomService {
    private String name;
    private String description;
    private MultipartFile image;
}
