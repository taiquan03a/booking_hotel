package com.hotel.booking.controller;

import com.hotel.booking.dto.rankRoom.CreateRankRoomRequest;
import com.hotel.booking.dto.rankRoom.EditRankRoomRequest;
import com.hotel.booking.service.CloudinaryService;
import com.hotel.booking.service.RoomRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/rank")
public class RoomRankController {
    @Autowired
    private CloudinaryService cloudinaryService;

    final private RoomRankService roomRankService;
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = String.valueOf(cloudinaryService.uploadImage(file,"Room"));
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Image upload failed");
        }
    }
    @PostMapping()
    public ResponseEntity<?> createRank(@ModelAttribute CreateRankRoomRequest request, Principal principal) throws IOException {
        return roomRankService.createRoomRank(request,principal);
    }
    @PutMapping()
    public ResponseEntity<?> editRank(@ModelAttribute EditRankRoomRequest request, Principal principal) throws IOException {
        return roomRankService.editRoomRank(request,principal);
    }
    @GetMapping("admin")
    public ResponseEntity<?> getListByAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ){
        return roomRankService.getListByAdmin(page,size,sortBy,direction);
    }
    @GetMapping()
    public ResponseEntity<?> getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return roomRankService.getList(page,size);
    }
    @GetMapping("active/{id}")
    public ResponseEntity<?> doActive(@PathVariable int id){
        return roomRankService.active(id);
    }
}
