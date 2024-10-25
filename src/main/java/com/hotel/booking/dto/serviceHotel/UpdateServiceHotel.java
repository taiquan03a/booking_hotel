package com.hotel.booking.dto.serviceHotel;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@Data
@Getter
@Setter
public class UpdateServiceHotel {
    private Integer serviceHotelId;
    private String name;
    private String location;
    private String capacity;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
    private MultipartFile image;
    private Integer categoryId;
}
