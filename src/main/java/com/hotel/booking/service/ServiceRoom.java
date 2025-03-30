package com.hotel.booking.service;

import com.hotel.booking.dto.roomService.AddRoomService;
import com.hotel.booking.dto.roomService.UpdateRoomService;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface ServiceRoom {
    ResponseEntity<?> view();
    ResponseEntity<?> viewDetail(int id);
    ResponseEntity<?> add(AddRoomService addRoomService) throws IOException;
    ResponseEntity<?> update(UpdateRoomService updateRoomService) throws IOException;
    ResponseEntity<?> delete(int id);
}
