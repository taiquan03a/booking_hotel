package com.hotel.booking.dto.serviceHotel;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@Data
public class CreateServiceHotel {
    private String name;
    private String location;
    private String capacity;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
    private MultipartFile image;
    private Integer categoryId;
    private Integer price;
}
