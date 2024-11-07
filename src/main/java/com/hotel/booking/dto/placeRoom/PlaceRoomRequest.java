package com.hotel.booking.dto.placeRoom;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PlaceRoomRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String country;
    private String note;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    List<RoomPlace> listPlace;
}
