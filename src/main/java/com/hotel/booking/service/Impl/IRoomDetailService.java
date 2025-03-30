package com.hotel.booking.service.Impl;

import com.hotel.booking.dto.ApiResponse;
import com.hotel.booking.model.BookingRoom;
import com.hotel.booking.model.Enum.RoomStatus;
import com.hotel.booking.dto.roomDetail.CreateRoomDetail;
import com.hotel.booking.dto.roomDetail.EditRoomDetail;
import com.hotel.booking.exception.AppException;
import com.hotel.booking.exception.ErrorCode;
import com.hotel.booking.mapping.RoomDetailMapper;
import com.hotel.booking.model.RoomDetail;
import com.hotel.booking.model.User;
import com.hotel.booking.repository.RoomDetailRepository;
import com.hotel.booking.service.RoomDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IRoomDetailService implements RoomDetailService {
    private final RoomDetailRepository roomDetailRepository;

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("get all room details")
                                .data(RoomDetailMapper.INSTANCE.toRoomDetailResponseList(roomDetailRepository.findAll()))
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> createRoom(CreateRoomDetail roomDetail, Principal principal) {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if(roomDetailRepository.existsByRoomNumber(roomDetail.getRoomNumber())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ApiResponse.builder()
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .message(String.valueOf(HttpStatus.BAD_REQUEST))
                                    .description("Số phòng đã tồn tại.")
                                    .build()
                    );
        }
        RoomDetail roomDetailEntity = RoomDetail.builder()
                .roomNumber(roomDetail.getRoomNumber())
                .roomCode(null)
                .room(null)
                .capacity(roomDetail.getCapacity())
                .location(roomDetail.getLocation())
                .status(String.valueOf(RoomStatus.AVAILABLE))
                .createBy(user.getEmail())
                .createAt(LocalDateTime.now())
                .build();
        roomDetailRepository.save(roomDetailEntity);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .message("Successfully create room number")
                                .data(RoomDetailMapper.INSTANCE.toRoomDetailResponse(roomDetailEntity))
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> editRoom(EditRoomDetail roomDetail,Principal principal) {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        RoomDetail roomDetailEntity = roomDetailRepository.findById(roomDetail.getRoomDetailId()).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        if(!roomDetailEntity.getRoomNumber().equals(roomDetail.getRoomNumber())) {
            if(roomDetailRepository.existsByRoomNumber(roomDetail.getRoomNumber())) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(
                                ApiResponse.builder()
                                        .statusCode(HttpStatus.BAD_REQUEST.value())
                                        .message(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                                        .description("Số phòng đã tồn tại.")
                                        .build()
                        );
            }
        }
        roomDetailEntity.setCapacity(roomDetail.getCapacity());
        roomDetailEntity.setLocation(roomDetail.getLocation());
        roomDetailEntity.setRoomNumber(roomDetail.getRoomNumber());
        roomDetailEntity.setUpdateBy(user.getEmail());
        roomDetailEntity.setUpdateAt(LocalDateTime.now());
        roomDetailRepository.save(roomDetailEntity);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully edit room number")
                                .data(RoomDetailMapper.INSTANCE.toRoomDetailResponse(roomDetailEntity))
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> deleteRoom(int id) {
        RoomDetail roomDetail = roomDetailRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        roomDetail.setRoom(null);
        roomDetailRepository.delete(roomDetail);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully remove room " + id)
                                .data(RoomDetailMapper.INSTANCE.toRoomDetailResponse(roomDetail))
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> deleteRoomDetail(int id) {
        roomDetailRepository.deleteById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully delete room number" + id)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> viewRoom() {
        List<RoomDetail> roomDetailList = roomDetailRepository.findAll();
        for(RoomDetail roomDetail : roomDetailList) {
            Set<BookingRoom> bookingRoomSet = roomDetail.getBookingRooms().stream()
                    .filter(br -> br.getCheckin().isBefore(LocalDateTime.now()) || br.getCheckin().isEqual(LocalDateTime.now()))
                    .filter(br -> br.getCheckout().isAfter(LocalDateTime.now()) || br.getCheckout().isEqual(LocalDateTime.now()))
                    .collect(Collectors.toSet());
            if (!bookingRoomSet.isEmpty()){
                roomDetail.setStatus(
                        bookingRoomSet.stream()
                                .max(Comparator.comparing(BookingRoom::getStatusTime))
                                .get().getStatus());
            }
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully get list room number")
                                .data(
                                        RoomDetailMapper.
                                                INSTANCE.toRoomDetailResponseList(
                                                        roomDetailList))
                                .build()
                );
    }
}
