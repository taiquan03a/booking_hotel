package com.hotel.booking.controller;

import com.hotel.booking.dto.placeRoom.PlaceRoomRequest;
import com.hotel.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    final private RoomService roomService;

    @PostMapping("place")
    public ResponseEntity<?> place(@RequestBody PlaceRoomRequest placeRoomRequest, Principal principal) {
        return roomService.placeRoom(placeRoomRequest, principal);
    }

}
