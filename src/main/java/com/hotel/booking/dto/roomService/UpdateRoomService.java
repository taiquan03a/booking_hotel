package com.hotel.booking.dto.roomService;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateRoomService {
    private int id;
        private String name;
        private String description;
        private MultipartFile image;

}
