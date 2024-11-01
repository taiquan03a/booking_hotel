package com.hotel.booking.service;

import com.hotel.booking.dto.roomDetail.CreateRoomDetail;
import com.hotel.booking.dto.roomDetail.EditRoomDetail;
import com.hotel.booking.model.Room;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface RoomDetailService {
    ResponseEntity<?> getAll();
    ResponseEntity<?> createRoom(CreateRoomDetail roomDetail, Principal principal);
    ResponseEntity<?> editRoom(EditRoomDetail roomDetail,Principal principal);
    ResponseEntity<?> deleteRoom(int id);
    ResponseEntity<?> viewRoom();
}
