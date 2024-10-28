package com.hotel.booking.dto.room;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class RoomResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer price;
    private Integer adultNumber;
    private Integer adultMax;
    private String roomRank;
    private Integer quantity;
    private Double rate;
    private Boolean active;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime createAt;
    private String createBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime updateAt;
    private String updateBy;
}
