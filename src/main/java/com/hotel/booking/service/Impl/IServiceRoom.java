package com.hotel.booking.service.Impl;

import com.hotel.booking.dto.ApiResponse;
import com.hotel.booking.dto.roomService.AddRoomService;
import com.hotel.booking.dto.roomService.UpdateRoomService;
import com.hotel.booking.exception.AppException;
import com.hotel.booking.exception.ErrorCode;
import com.hotel.booking.model.RoomServiceModel;
import com.hotel.booking.repository.RoomServiceModelRepository;
import com.hotel.booking.service.CloudinaryService;
import com.hotel.booking.service.ServiceRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IServiceRoom implements ServiceRoom {
    private final RoomServiceModelRepository roomServiceModelRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public ResponseEntity<?> view() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("view list room service")
                                .data(roomServiceModelRepository.findAll())
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> viewDetail(int id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("view detail service room")
                                .data(roomServiceModelRepository.findById(id))
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> add(AddRoomService addRoomService) throws IOException {
        String ulr = cloudinaryService.uploadImage(addRoomService.getImage(),"rooms");

        RoomServiceModel roomServiceModel = RoomServiceModel.builder()
                .name(addRoomService.getName())
                .description(addRoomService.getDescription())
                .icon(ulr)
                .active(true)
                .createAt(LocalDateTime.now())
                .build();
        roomServiceModelRepository.save(roomServiceModel);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("add service room")
                                .data(roomServiceModel)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> update(UpdateRoomService updateRoomService) throws IOException {
        RoomServiceModel roomServiceModel = roomServiceModelRepository
                .findById(updateRoomService.getId())
                .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        String url = cloudinaryService.uploadImage(updateRoomService.getImage(),"rooms");
        roomServiceModel.setName(updateRoomService.getName());
        roomServiceModel.setDescription(updateRoomService.getDescription());
        roomServiceModel.setIcon(url);
        roomServiceModel.setUpdateAt(LocalDateTime.now());
        roomServiceModelRepository.save(roomServiceModel);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("update service room")
                                .data(roomServiceModel)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> delete(int id) {
        RoomServiceModel roomServiceModel = roomServiceModelRepository
                .findById(id)
                .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        roomServiceModel.setActive(!roomServiceModel.getActive());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("thanh cong")
                                .data(roomServiceModel)
                                .build()
                );
    }
}
