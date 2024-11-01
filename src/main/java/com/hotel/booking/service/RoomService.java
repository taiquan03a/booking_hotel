package com.hotel.booking.service;

import com.hotel.booking.dto.placeRoom.PlaceRoomRequest;
import com.hotel.booking.dto.room.CreateRoomRequest;
import com.hotel.booking.dto.room.EditRoomRequest;
import com.hotel.booking.model.Room;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.Principal;

public interface RoomService {
    ResponseEntity<?> createRoom(CreateRoomRequest createRoomRequest, Principal principal);
    ResponseEntity<?> deleteRoom(int roomId,Principal principal);
    ResponseEntity<?> editRoom(EditRoomRequest room, Principal principal);
    ResponseEntity<?> getRoom(int id);
    ResponseEntity<?> getAllRoomsByAdmin(int page, int size,String sortBy,String direction);
    ResponseEntity<?> getRoomById(int id);
    ResponseEntity<?> getRoomByRank(int rankId);
    ResponseEntity<?> getAllByAdmin(String rankId);
    ResponseEntity<?> getAllPolicyType();
    ResponseEntity<?> placeRoom(PlaceRoomRequest placeRoomRequest, Principal principal);
    ResponseEntity<?> getAllServiceRoom();
}
