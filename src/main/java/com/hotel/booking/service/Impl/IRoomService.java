package com.hotel.booking.service.Impl;

import com.hotel.booking.dto.ApiResponse;
import com.hotel.booking.dto.placeRoom.PlaceRoomRequest;
import com.hotel.booking.dto.policy.PolicyDto;
import com.hotel.booking.dto.room.*;
import com.hotel.booking.exception.AppException;
import com.hotel.booking.exception.ErrorCode;
import com.hotel.booking.mapping.PolicyMapper;
import com.hotel.booking.mapping.RoomDetailMapper;
import com.hotel.booking.mapping.RoomMapper;
import com.hotel.booking.mapping.RoomServiceMapper;
import com.hotel.booking.model.*;
import com.hotel.booking.repository.*;
import com.hotel.booking.service.CloudinaryService;
import com.hotel.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IRoomService implements RoomService {
    final private RoomRepository roomRepository;
    final private RoomRankRepository roomRankRepository;
    final private RoomServiceModelRepository roomServiceModelRepository;
    final private CloudinaryService cloudinaryService;
    final private PolicyRepository policyRepository;
    final private RoomDetailRepository roomDetailRepository;
    final private PolicyTypeRepository policyTypeRepository;
    private final PolicyMapper policyMapper;
    private final RoomDetailMapper roomDetailMapper;
    private final RoomServiceMapper roomServiceMapper;
    private final RoomMapper roomMapper;

    @Override
    public ResponseEntity<?> createRoom(CreateRoomRequest createRoomRequest, Principal principal) {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if (user == null) throw new AppException(ErrorCode.NOT_FOUND);
        List<Policy> policies = new ArrayList<>();
        for (PolicyDto dto : createRoomRequest.getPolicyList()) {
            Policy policy = Policy.builder()
                    .content(dto.getContent())
                    .description(dto.getDescription())
                    .type(policyTypeRepository.findById(dto.getTypeId()).get())
                    .createAt(LocalDateTime.now())
                    .createBy(user.getEmail())
                    .build();
            policies.add(policy);
        }
        List<RoomServiceModel> roomServices = roomServiceModelRepository.findAllById(createRoomRequest.getServiceList());
        Room room = Room.builder()
                .name(createRoomRequest.getName())
                .description(createRoomRequest.getDescription())
                .price(createRoomRequest.getPrice())
                .adultNumber(createRoomRequest.getAdultNumber())
                .adultMax(createRoomRequest.getAdultMax())
                .service(roomServices)
                .quantity(createRoomRequest.getRoomList().size())
                .roomRank(roomRankRepository.findById(createRoomRequest.getRoomRank()).get())
                .active(true)
                .createAt(LocalDateTime.now())
                .createBy(user.getEmail())
                .build();
        roomRepository.save(room);
        List<RoomDetail> roomDetails = roomDetailRepository.findAllById(createRoomRequest.getRoomList());
        for(RoomDetail detail : roomDetails) {
            detail.setRoom(room);
            detail.setRoomCode(detail.getLocation()+"_"+detail.getRoomNumber());
            roomDetailRepository.save(detail);
        }
//        for (Integer roomNumber : createRoomRequest.getRoomList()) {
//            RoomDetail roomDetail = RoomDetail.builder()
//                    .room(room)
//                    .roomCode(String.valueOf(roomNumber))
//                    .roomNumber(roomNumber)
//                    .status(String.valueOf(RoomStatus.AVAILABLE))
//                    .createAt(LocalDateTime.now())
//                    .createBy(user.getEmail())
//                    .build();
//            roomDetails.add(roomDetail);
//            roomDetailRepository.save(roomDetail);
//        }
        for (Policy policy : policies) {
            policy.setRoom(room);
            policyRepository.save(policy);
        }
        room.setPolicies(policies);
        room.setRoomDetails(roomDetails);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .message("Successfully add a new room")
                                .data(room)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> deleteRoom(int roomId, Principal principal) {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Room room = roomRepository.findById(roomId).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        room.setActive(!room.getActive());
        room.setUpdateAt(LocalDateTime.now());
        room.setUpdateBy(user.getEmail());
        roomRepository.save(room);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully active/inactive a room")
                                .data(room)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> editRoom(EditRoomRequest room, Principal principal) {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Room roomCurrent = roomRepository.findById(room.getRoomId()).orElseThrow(() -> new IllegalArgumentException("room not found"));
        if (user == null) throw new AppException(ErrorCode.NOT_FOUND);
        List<Policy> policies = room.getPolicyList().stream()
                .map(policyDto -> {
                    return Policy.builder()
                            .room(roomCurrent)
                            .content(policyDto.getContent())
                            .description(policyDto.getDescription())
                            .type(policyTypeRepository.findById(policyDto.getTypeId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND)))
                            .createAt(LocalDateTime.now())
                            .createBy(user.getEmail())
                            .build();
                }).toList();
//        List<RoomDetail> roomDetails = room.getRoomList().stream()
//                .map(roomNumber -> {
//                    return RoomDetail.builder()
//                            .room(roomCurrent)
//                            .roomCode(String.valueOf(roomNumber))
//                            .roomNumber(roomNumber)
//                            .status(String.valueOf(RoomStatus.AVAILABLE))
//                            .createAt(LocalDateTime.now())
//                            .createBy(user.getEmail())
//                            .build();
//                }).toList();
        List<RoomDetail> roomDetailCurrent = roomCurrent.getRoomDetails();
        for(RoomDetail detail : roomDetailCurrent) {
            detail.setRoom(null);
            roomDetailRepository.save(detail);
        }
        List<RoomDetail> roomDetails = roomDetailRepository.findAllById(room.getRoomList());
        for(RoomDetail detail : roomDetails) {
            detail.setRoom(roomCurrent);
            detail.setRoomCode(detail.getLocation()+"_"+detail.getRoomNumber());
            roomDetailRepository.save(detail);
        }
        List<Policy> existingPolicies = roomCurrent.getPolicies();
        existingPolicies.removeIf(existingPolicy ->
                policies.stream().noneMatch(newPolicy -> newPolicy.getId() != null && newPolicy.getId().equals(existingPolicy.getId())));

        existingPolicies.removeIf(existingPolicy ->
                policies.stream().noneMatch(newPolicy ->
                        newPolicy.getId() != null && newPolicy.getId().equals(existingPolicy.getId())));

        for (Policy newPolicy : policies) {
            if (newPolicy.getId() == null) {
                existingPolicies.add(newPolicy);
            } else {
                existingPolicies.stream()
                        .filter(existingPolicy -> existingPolicy.getId().equals(newPolicy.getId()))
                        .findFirst()
                        .ifPresent(existingPolicy -> {
                            existingPolicy.setContent(newPolicy.getContent());
                            existingPolicy.setDescription(newPolicy.getDescription());
                            existingPolicy.setType(newPolicy.getType());
                        });
            }
        }
        List<RoomDetail> existingRoomDetails = roomCurrent.getRoomDetails();
        existingRoomDetails.removeIf(existingRoomDetail ->
                roomDetails.stream().noneMatch(newRoomDetail ->
                        newRoomDetail.getId() != null && newRoomDetail.getId().equals(existingRoomDetail.getId())));

        for (RoomDetail newRoomDetail : roomDetails) {
            if (newRoomDetail.getId() == null) {
                existingRoomDetails.add(newRoomDetail);
            } else {
                existingRoomDetails.stream()
                        .filter(existingRoomDetail -> existingRoomDetail.getId().equals(newRoomDetail.getId()))
                        .findFirst()
                        .ifPresent(existingRoomDetail -> {
                            existingRoomDetail.setRoomCode(newRoomDetail.getRoomCode());
                            existingRoomDetail.setRoomNumber(newRoomDetail.getRoomNumber());
                        });
            }
        }
        List<RoomServiceModel> roomServices = roomServiceModelRepository.findAllById(room.getServiceList());
        roomCurrent.setName(room.getName());
        roomCurrent.setDescription(room.getDescription());
        roomCurrent.setPrice(room.getPrice());
        roomCurrent.setAdultNumber(room.getAdultNumber());
        roomCurrent.setAdultMax(room.getAdultMax());
        roomCurrent.setService(roomServices);
        roomCurrent.setQuantity(room.getRoomList().size());
        roomCurrent.setRoomRank(roomRankRepository.findById(room.getRoomRank()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND)));
        roomCurrent.setPolicies(existingPolicies);
        roomCurrent.setRoomDetails(existingRoomDetails);
        roomCurrent.setUpdateAt(LocalDateTime.now());
        roomCurrent.setUpdateBy(user.getEmail());
        roomRepository.save(roomCurrent);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully edit a room")
                                .data(room)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> getRoom(int id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        RoomAdminResponse roomResponse = RoomAdminResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .price(room.getPrice())
                .roomRank(room.getRoomRank().getName())
                .adultNumber(room.getAdultNumber())
                .adultMax(room.getAdultMax())
                .quantity(room.getQuantity())
                .rate(room.getRate())
                .policyList(policyMapper.toResponseList(room.getPolicies()))
                .roomDetailList(RoomDetailMapper.INSTANCE.toRoomDetailResponseList(room.getRoomDetails()))
                .roomServiceList(RoomServiceMapper.INSTANCE.toRoomServiceResponseList(room.getService()))
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully a room detail")
                                .data(roomResponse)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> getAllRoomsByAdmin(int page, int size, String sortBy, String direction) {
        return null;
    }

    @Override
    public ResponseEntity<?> getRoomById(int id) {
        return null;
    }

    @Override
    public ResponseEntity<?> getRoomByRank(int rankId) {
        RoomRank rank = roomRankRepository.findById(rankId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        List<Room> roomList = roomRepository.findAllByRoomRank(rank);
        List<RoomUserResponse> roomUserResponseList = new ArrayList<>();
        for (Room room : roomList) {
            List<RoomDetail> roomAvailable = room.getRoomDetails()
                    .stream()
                    .filter(roomDetail -> String.valueOf(RoomStatus.AVAILABLE).equals(roomDetail.getStatus()))
                    .toList();
            if (!roomAvailable.isEmpty()) {
                RoomUserResponse roomResponse = RoomUserResponse.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .description(room.getDescription())
                        .price(room.getPrice())
                        .adultNumber(room.getAdultNumber())
                        .adultMax(room.getAdultMax())
                        .quantity(roomAvailable.size())
                        .policyList(policyMapper.toResponseList(room.getPolicies()))
                        .roomServiceList(RoomServiceMapper.INSTANCE.toRoomServiceResponseList(room.getService()))
                        .build();
                roomUserResponseList.add(roomResponse);
            }

        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully List room by rank room for user")
                                .data(roomUserResponseList)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> getAllByAdmin(String rankId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully List room by rank room for admin")
                                .data(
                                        roomMapper.toRoomResponseList(
                                                roomRepository.findAll().
                                                        stream()
                                                        .filter(room -> rankId == null || room.getRoomRank().getId() == Integer.parseInt(rankId) && room.getActive())
                                                        .collect(Collectors.toList())
                                        )
                                )
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> getAllPolicyType() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully List room by rank room for user")
                                .data(policyTypeRepository.findAll())
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> placeRoom(PlaceRoomRequest placeRoomRequest, Principal principal) {
        User user = (principal != null) ? (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal() : null;

        return null;
    }

    @Override
    public ResponseEntity<?> getAllServiceRoom() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully List service for room")
                                .data(RoomServiceMapper.INSTANCE.toRoomServiceResponseList(roomServiceModelRepository.findAll().stream().filter(RoomServiceModel::getActive).toList()))
                                .build()
                );
    }

}
