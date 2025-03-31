package com.hotel.booking.dto.serviceHotel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceDto {
    private int id;
    private String name;
    private String location;
    private String capacity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss",timezone = "Asia/Ho_Chi_Minh")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss",timezone = "Asia/Ho_Chi_Minh")
    private LocalTime endTime;
    private String description;
    private String image;
    private int price;
}
