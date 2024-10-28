package com.hotel.booking.controller;

import com.hotel.booking.dto.room.CreateRoomRequest;
import com.hotel.booking.dto.room.EditRoomRequest;
import com.hotel.booking.service.RoomService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/room")
@RequiredArgsConstructor
public class RoomController {
    final private RoomService roomService;

    @PostMapping("create")
    ResponseEntity<?> addRoom(@RequestBody CreateRoomRequest room, Principal principal) {
        return roomService.createRoom(room,principal);
    }
    @PutMapping("edit")
    ResponseEntity<?> updateRoom(@RequestBody EditRoomRequest room, Principal principal) {
        return roomService.editRoom(room,principal);
    }
    @GetMapping("/{id}")
    ResponseEntity<?> getRoomById(@PathVariable("id") int id, Principal principal) {
        return roomService.getRoom(id);
    }
    @GetMapping("/getByRank/{rankId}")
    ResponseEntity<?> getRoomByRank(@PathVariable() int rankId) {
        return roomService.getRoomByRank(rankId);
    }
    @GetMapping("admin/getAll")
    ResponseEntity<?> getRoomByRankAdmin(@RequestParam(required = false) String rankId) {
        return roomService.getAllByAdmin(rankId);
    }
    @PostMapping("admin/active/{roomId}")
    ResponseEntity<?> getRoomByRankAdminActive(@PathVariable int roomId, Principal principal) {
        return roomService.deleteRoom(roomId,principal);
    }
}
