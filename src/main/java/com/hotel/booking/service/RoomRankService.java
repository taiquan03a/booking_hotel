package com.hotel.booking.service;

import com.hotel.booking.dto.rankRoom.CreateRankRoomRequest;
import com.hotel.booking.dto.rankRoom.EditRankRoomRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;

public interface RoomRankService {
    ResponseEntity<?> getList(int roomNumber,LocalDate startDate, LocalDate endDate,int page, int size);
    ResponseEntity<?> createRoomRank(CreateRankRoomRequest request, Principal principal) throws  IOException;
    ResponseEntity<?> editRoomRank(EditRankRoomRequest request,Principal principal) throws IOException;
    ResponseEntity<?> active(int rankId);
    ResponseEntity<?> getListByAdmin();
    ResponseEntity<?> filter(LocalDate startDate, LocalDate endDate, int roomNumber,int page, int size);
    ResponseEntity<?> getAllBed();
    ResponseEntity<?> getAllAmenity();
}
