package com.hotel.booking.controller;

import com.hotel.booking.dto.room.CreateRoomRequest;
import com.hotel.booking.dto.room.EditRoomRequest;
import com.hotel.booking.dto.roomDetail.CreateRoomDetail;
import com.hotel.booking.dto.roomDetail.EditRoomDetail;
import com.hotel.booking.service.RoomDetailService;
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
    final private RoomDetailService roomDetailService;

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
    @GetMapping("admin/active/{roomId}")
    ResponseEntity<?> getRoomByRankAdminActive(@PathVariable int roomId, Principal principal) {
        return roomService.deleteRoom(roomId,principal);
    }
    @GetMapping("admin/policy_type/getAll")
    ResponseEntity<?> getRoomByRankAdminPolicyType() {
        return roomService.getAllPolicyType();
    }
    @GetMapping("admin/room_service/all")
    ResponseEntity<?> getAllRoomByRankAdmin() {
        return roomService.getAllServiceRoom();
    }
    //        room detail

    @GetMapping("admin/room_number/get")
    ResponseEntity<?> getRoomNumber() {
        return roomDetailService.viewRoom();
    }
    @PostMapping("admin/room_number/create")
    ResponseEntity<?> createRoom(@RequestBody CreateRoomDetail room, Principal principal) {
        return roomDetailService.createRoom(room,principal);
    }
    @PutMapping("admin/room_number/edit")
    ResponseEntity<?> editRoom(@RequestBody EditRoomDetail room, Principal principal) {
        return roomDetailService.editRoom(room,principal);
    }
    @GetMapping("admin/room_number/remove_room/{id}")
    ResponseEntity<?> removeRoom(@PathVariable int id) {
        return roomDetailService.deleteRoom(id);
    }

    @GetMapping("admin/room_number/all")
    ResponseEntity<?> getAllRoom() {
        return roomDetailService.getAll();
    }

}
