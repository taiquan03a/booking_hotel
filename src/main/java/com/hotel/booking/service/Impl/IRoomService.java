package com.hotel.booking.service.Impl;

import com.hotel.booking.dto.ApiResponse;
import com.hotel.booking.dto.placeRoom.*;
import com.hotel.booking.dto.policy.PolicyDto;
import com.hotel.booking.dto.rankRoom.RankRoomPlaceResponse;
import com.hotel.booking.dto.room.*;
import com.hotel.booking.dto.roomDetail.RoomDetailResponse;
import com.hotel.booking.dto.roomService.RoomServiceResponse;
import com.hotel.booking.dto.roomService.ServiceRoomRequest;
import com.hotel.booking.exception.AppException;
import com.hotel.booking.exception.ErrorCode;
import com.hotel.booking.mapping.*;
import com.hotel.booking.model.*;
import com.hotel.booking.model.Enum.PolicyTypeEnum;
import com.hotel.booking.model.Enum.RoomStatus;
import com.hotel.booking.repository.*;
import com.hotel.booking.service.CloudinaryService;
import com.hotel.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final ServiceRoomRepository serviceRoomRepository;

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
        //List<RoomServiceModel> roomServices = roomServiceModelRepository.findAllById(createRoomRequest.getServiceList());
        List<Integer> serviceIds = createRoomRequest.getServiceList()
                .stream()
                .map(ServiceRoomRequest::getServiceId)
                .collect(Collectors.toList());
        List<RoomServiceModel> roomServices = roomServiceModelRepository.findAllById(serviceIds);
        List<ServiceRoom> serviceRoomList = new ArrayList<>();
        for (ServiceRoomRequest request : createRoomRequest.getServiceList()) {
            RoomServiceModel serviceModel = roomServiceModelRepository.findById(request.getServiceId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
            roomServices.add(serviceModel);
            ServiceRoom serviceRoom = ServiceRoom.builder()
                    .service(serviceModel)
                    .price(request.getPrice())
                    .build();
            serviceRoomList.add(serviceRoom);
        }
        Room room = Room.builder()
                .name(createRoomRequest.getName())
                .description(createRoomRequest.getDescription())
                .price(createRoomRequest.getPrice())
                .adultNumber(createRoomRequest.getAdultNumber())
                .adultMax(createRoomRequest.getAdultMax())
                //.service(roomServices)
                .quantity(createRoomRequest.getRoomList().size())
                .roomRank(roomRankRepository.findById(createRoomRequest.getRoomRank()).get())
                .active(true)
                .createAt(LocalDateTime.now())
                .createBy(user.getEmail())
                .build();
        roomRepository.save(room);
        List<RoomDetail> roomDetails = roomDetailRepository.findAllById(createRoomRequest.getRoomList());
        for (RoomDetail detail : roomDetails) {
            detail.setRoom(room);
            detail.setRoomCode(detail.getLocation() + "_" + detail.getRoomNumber());
            roomDetailRepository.save(detail);
        }
        serviceRoomList.forEach(serviceRoom -> {
            serviceRoom.setRoom(room);
        });
        serviceRoomRepository.saveAll(serviceRoomList);
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
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
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
                    System.out.println(policyDto.getTypeId());
                    return Policy.builder()
                            .room(roomCurrent)
                            .content(policyDto.getContent())
                            .description(policyDto.getDescription())
                            .type(policyTypeRepository.findById(policyDto.getTypeId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND)))
                            .createAt(LocalDateTime.now())
                            .createBy(user.getEmail())
                            .build();
                }).toList();
        for (PolicyType type : policyTypeRepository.findAll()) {
            List<Policy> matchingPolicies = policies.stream()
                    .filter(policy -> policy.getType().getType().equals(type.getType()))
                    .toList();
            List<Policy> currentPolicy = roomCurrent.getPolicies().stream()
                    .filter(policy -> policy.getType().getType().equals(type.getType()))
                    .toList();
            if (!matchingPolicies.isEmpty() && !currentPolicy.isEmpty()) {
                currentPolicy.get(0).setContent(matchingPolicies.get(0).getContent());
                currentPolicy.get(0).setDescription(matchingPolicies.get(0).getDescription());
                policyRepository.save(currentPolicy.get(0));
            }
            if (!matchingPolicies.isEmpty() && currentPolicy.isEmpty()) {
                policyRepository.save(matchingPolicies.get(0));
            }
            if (matchingPolicies.isEmpty() && !currentPolicy.isEmpty()) {
                System.out.println("policy-> " + currentPolicy.get(0).getId());
                policyRepository.deleteById(currentPolicy.get(0).getId());
                policyRepository.flush();
            }
        }
        List<RoomDetail> roomDetailCurrent = roomCurrent.getRoomDetails();
        for (RoomDetail detail : roomDetailCurrent) {
            detail.setRoom(null);
            roomDetailRepository.save(detail);
        }
        List<RoomDetail> roomDetails = roomDetailRepository.findAllById(room.getRoomList());
        for (RoomDetail detail : roomDetails) {
            detail.setRoom(roomCurrent);
            detail.setRoomCode(detail.getLocation() + "_" + detail.getRoomNumber());
            roomDetailRepository.save(detail);
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
        //List<RoomServiceModel> roomServices = roomServiceModelRepository.findAllById(room.getServiceList());
        List<Integer> serviceIds = room.getServiceList()
                .stream()
                .map(ServiceRoomRequest::getServiceId)
                .collect(Collectors.toList());
        List<RoomServiceModel> roomServices = roomServiceModelRepository.findAllById(serviceIds);
        List<ServiceRoom> currentService = serviceRoomRepository.findAllByRoom(roomCurrent);
        for (ServiceRoom serviceRoom1 : currentService) {
            if (serviceRoom1.getId() == null) {
                serviceRoomRepository.delete(serviceRoom1);
            }
        }
        //serviceRoomRepository.deleteAllById(currentService.stream().map(ServiceRoom::getId).collect(Collectors.toList()));
        roomCurrent.setName(room.getName());
        roomCurrent.setDescription(room.getDescription());
        roomCurrent.setPrice(room.getPrice());
        roomCurrent.setAdultNumber(room.getAdultNumber());
        roomCurrent.setAdultMax(room.getAdultMax());
        //roomCurrent.setService(roomServices);
        roomCurrent.setQuantity(room.getRoomList().size());
        roomCurrent.setRoomRank(roomRankRepository.findById(room.getRoomRank()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND)));
        //roomCurrent.setPolicies(existingPolicies);
        roomCurrent.setRoomDetails(existingRoomDetails);
        roomCurrent.setUpdateAt(LocalDateTime.now());
        roomCurrent.setUpdateBy(user.getEmail());
        roomRepository.save(roomCurrent);
        for (ServiceRoomRequest serviceRoomRequest : room.getServiceList()) {
            RoomServiceModel roomServiceModel = roomServiceModelRepository.findById(serviceRoomRequest.getServiceId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
            ServiceRoom serviceRoom = new ServiceRoom();
            serviceRoom.setPrice(serviceRoomRequest.getPrice());
            serviceRoom.setRoom(roomCurrent);
            serviceRoom.setService(roomServiceModel);
            serviceRoomRepository.save(serviceRoom);
        }
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
        List<RoomAdminResponse> roomResponseList = roomMapper.toRoomResponseList(
                roomRepository.findAll().
                        stream()
                        .filter(room -> rankId == null || room.getRoomRank().getId() == Integer.parseInt(rankId))
                        .collect(Collectors.toList()));
        for(RoomAdminResponse roomAdminResponse : roomResponseList) {
            Room room = roomRepository.findById(roomAdminResponse.getId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
            for(RoomServiceResponse serviceResponse:roomAdminResponse.getRoomServiceList()){
                RoomServiceModel service = roomServiceModelRepository.findById(serviceResponse.getId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
                ServiceRoom serviceRoom = serviceRoomRepository.findByRoomAndService(room,service);
                serviceResponse.setPrice(serviceRoom.getPrice());
            }
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully List room by rank room for admin")
                                .data(roomResponseList)
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
        List<Booking> bookingList = new ArrayList<>();
        for (RoomPlace roomPlace : placeRoomRequest.getListPlace()) {
            Room room = roomRepository.findById(roomPlace.getRoomId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
            Map<String, String> policyMap = new HashMap<>();
            for (Policy policy : room.getPolicies()) {
                policyMap.put(policy.getType().getType(), policy.getContent());
            }
            for (SelectRoom selectRoom : roomPlace.getListSelect()) {
                if (selectRoom.getAdults() + selectRoom.getChildren() > room.getAdultMax())
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(
                                    ApiResponse.builder()
                                            .statusCode(HttpStatus.BAD_REQUEST.value())
                                            .message("MAX_ADULT")
                                            .description("Số người vượt quá giới hạn vui lòng chọn lại.")
                                            .build()
                            );
                BookingRoom bookingRoom = BookingRoom.builder()
                        .sumAdult(selectRoom.getAdults())
                        .sumChildren(selectRoom.getChildren())
                        .sumInfant(selectRoom.getInfants())
                        .build();
                if (selectRoom.getAdults() > room.getAdultNumber()) {
                    int adultPlus = Integer.parseInt(policyMap.get(String.valueOf(PolicyTypeEnum.ADULT)));
                    bookingRoom.setAdultSurcharge((selectRoom.getAdults() - room.getAdultNumber()) * adultPlus);
                }
                
            }
        }
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

    @Override
    public ResponseEntity<?> searchRoomAdmin(LocalDate checkInDate, LocalDate checkOutDate, int adults, int children, int rankId) {
        LocalDateTime checkin = checkInDate.atTime(LocalTime.of(14, 0));
        LocalDateTime checkout = checkOutDate.atTime(LocalTime.of(12, 0));
        List<RoomRank> roomRankList = new ArrayList<>();
        if (rankId != 0) {
            RoomRank roomRank = roomRankRepository.findById(rankId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
            if (roomRank.getActive()) roomRankList.add(roomRank);
        } else {
            roomRankList = roomRankRepository.findAll().stream().filter(RoomRank::getActive).collect(Collectors.toList());
        }
        int totalRoom = 0, availableRoom = 0, bookedRoom = 0;
        List<RankRoomPlaceResponse> rankRoomPlaceResponses = new ArrayList<>();
        for (RoomRank roomRank : roomRankList) {
            List<Room> roomActiveList = roomRepository.findAllByRoomRank(roomRank).stream().filter(Room::getActive).toList();
            List<RoomPlaceResponse> roomPlaceResponseList = new ArrayList<>();
            for (Room room : roomActiveList) {
                if (adults + children > room.getAdultMax()) return null;
                List<RoomDetail> roomDetails = roomDetailRepository.findAvailableRooms(checkin, checkout, room);
                RoomPlaceResponse roomPlace = RoomPlaceResponse.builder()
                        .roomId(room.getId())
                        .roomName(room.getName())
                        .adultNumber(room.getAdultNumber())
                        .adultMax(room.getAdultMax())
                        .build();
                availableRoom += roomDetails.size();
                bookedRoom += roomDetailRepository.findCurrentlyBookedRooms(room).size();
                totalRoom += room.getRoomDetails().size();
                if (!roomDetails.isEmpty()) {
                    List<RoomDetailResponse> roomDetailResponseList = RoomDetailMapper.INSTANCE.toRoomDetailResponseList(roomDetails);
                    roomPlace.setRoomNumberList(roomDetailResponseList);
                    roomPlaceResponseList.add(roomPlace);
                }
            }
            RankRoomPlaceResponse rankRoomPlaceResponse = RankRoomPlaceResponse.builder()
                    .id(roomRank.getId())
                    .name(roomRank.getName())
                    .area(roomRank.getArea())
                    .amenity(RoomRankMapper.INSTANCE.toAmenityDtoList(roomRank.getAmenity()))
                    .roomPlaces(roomPlaceResponseList)
                    .build();
            rankRoomPlaceResponses.add(rankRoomPlaceResponse);
        }
        SearchRoomResponse searchRoomResponse = SearchRoomResponse.builder()
                .availableRoom(availableRoom)
                .bookedRoom(bookedRoom)
                .totalRoom(totalRoom)
                .rankList(rankRoomPlaceResponses)
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully search room")
                                .data(searchRoomResponse)
                                .build()
                );
    }

}
