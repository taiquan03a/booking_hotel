package com.hotel.booking.service.Impl;

import com.hotel.booking.dto.ApiResponse;
import com.hotel.booking.dto.bed.BedDto;
import com.hotel.booking.dto.rankRoom.CreateRankRoomRequest;
import com.hotel.booking.dto.rankRoom.EditRankRoomRequest;
import com.hotel.booking.dto.rankRoom.RankRoomResponseAdmin;
import com.hotel.booking.dto.rankRoom.RankRoomResponseUser;
import com.hotel.booking.model.Enum.RoomStatus;
import com.hotel.booking.exception.AppException;
import com.hotel.booking.exception.ErrorCode;
import com.hotel.booking.mapping.RoomRankMapper;
import com.hotel.booking.model.*;
import com.hotel.booking.repository.*;
import com.hotel.booking.service.CloudinaryService;
import com.hotel.booking.service.RoomRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IRoomRankService implements RoomRankService {

    final private AmenityRepository amenityRepository;
    final private RoomRankRepository roomRankRepository;
    final private CloudinaryService cloudinaryService;
    final private RoomBedRepository roomBedRepository;
    final private BedRepository bedRepository;
    final private ImageRepository imageRepository;
    private final RoomDetailRepository roomDetailRepository;

    @Override
    public ResponseEntity<?> getList(int roomNumber,LocalDate startDate, LocalDate endDate,int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomRank> ranks = roomRankRepository.findByActiveTrue(pageable);
        List<RankRoomResponseUser> rankRoomResponseUser = new ArrayList<>();
        for(RoomRank roomRank : ranks.getContent()) {
            List<Room> rooms = roomRank.getRooms();
            List<Room> activeRooms = new ArrayList<>();
            for(Room room : rooms){
                if(room.getActive()){
                    List<RoomDetail> roomDetails = roomDetailRepository.findAvailableRooms(startDate.atTime(14,0),endDate.atTime(12,0),room);
                    System.out.println(roomNumber + " " + roomDetails.size());
                    if(roomDetails.size() >= roomNumber){
                        activeRooms.add(room);
                    }
                }
            }
            if(!activeRooms.isEmpty()){
                int minPrice = activeRooms.stream()
                        .mapToInt(Room::getPrice)
                        .min().orElse(0);
                List<String> amenities = roomRank.getAmenity().stream().filter(Amenity::getActive).map(Amenity::getName).toList();
                List<BedDto> bedDtos = new ArrayList<>();
                for(RoomBed bed : roomRank.getRoomBeds()) {
                    BedDto bedDto = BedDto.builder()
                            .name(bed.getBed().getName())
                            .quantity(bed.getQuantity())
                            .build();
                    bedDtos.add(bedDto);
                }
                RankRoomResponseUser res =  RankRoomResponseUser.builder()
                        .id(roomRank.getId())
                        .name(roomRank.getName())
                        .price(minPrice)
                        .bed(bedDtos)
                        .image(roomRank.getImages().stream().map(Image::getPath).toList())
                        .area(roomRank.getArea())
                        .amenityList(amenities)
                        .build();
                rankRoomResponseUser.add(res);
            }
        }
        Page<RankRoomResponseUser> rankRoomResponseUserPage = new PageImpl<>(rankRoomResponseUser, pageable, ranks.getTotalElements());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully list room ranks for user")
                                .data(rankRoomResponseUserPage)
                                .build()
                );
    }
    public RankRoomResponseUser mapRankTODto(RoomRank roomRank,LocalDate startDate, LocalDate endDate, int roomNumber){
        List<Room> rooms = roomRank.getRooms();
        List<Room> activeRooms = new ArrayList<>();
        for(Room room : rooms){
            if(room.getActive()){
                List<RoomDetail> roomDetails = room.getRoomDetails().stream().filter(roomDetail -> RoomStatus.AVAILABLE.equals(roomDetail.getStatus())).toList();
                System.out.println(roomNumber + " " + roomDetails.size());
                if(roomDetails.size() >= roomNumber){
                    activeRooms.add(room);
                }
            }
        }
        int minPrice = activeRooms.stream()
                .mapToInt(Room::getPrice)
                .min().orElse(0);
        List<String> amenities = roomRank.getAmenity().stream().filter(Amenity::getActive).map(Amenity::getName).toList();
        List<BedDto> bedDtos = new ArrayList<>();
        for(RoomBed bed : roomRank.getRoomBeds()) {
            BedDto bedDto = BedDto.builder()
                    .name(bed.getBed().getName())
                    .quantity(bed.getQuantity())
                    .build();
            bedDtos.add(bedDto);
        }
        return RankRoomResponseUser.builder()
                .id(roomRank.getId())
                .name(roomRank.getName())
                .price(minPrice)
                .bed(bedDtos)
                .image(roomRank.getImages().stream().map(Image::getPath).toList())
                .area(roomRank.getArea())
                .amenityList(amenities)
                .build();
    }
    @Override
    public ResponseEntity<?> getListByAdmin() {
        List<RankRoomResponseAdmin> res = RoomRankMapper.INSTANCE.toRankRoomResponseAdminList(roomRankRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully list room ranks")
                                .data(res)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> filter(LocalDate startDate, LocalDate endDate, int roomNumber,int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomRank> ranks = roomRankRepository.findByActiveTrue(pageable);

        return null;
    }

    @Override
    public ResponseEntity<?> getAllBed() {
        List<Bed> x = bedRepository.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully list bed")
                                .data(x)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> getAllAmenity() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully list amenity")
                                .data(amenityRepository.findAll())
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> createRoomRank(CreateRankRoomRequest request, Principal principal) throws IOException {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if(user == null) throw new AppException(ErrorCode.NOT_FOUND);
        RoomRank roomRank = RoomRank.builder()
                .name(request.getName())
                .description(request.getDescription())
                .area(request.getArea())
                .amenity(amenityRepository.findAllById(request.getAmenityId()))
                .createAt(LocalDateTime.now())
                .createBy(user.getEmail())
                .build();
        roomRankRepository.save(roomRank);
        List<RoomBed> roomBedList = new ArrayList<>();
        for(String bedString : request.getBed()){
            String[] split = bedString.split(":");
            Bed bed = bedRepository.findById(Integer.parseInt(split[0])).get();
            RoomBed roomBed = RoomBed.builder()
                    .bed(bed)
                    .rank(roomRank)
                    .quantity(Integer.parseInt(split[1]))
                    .build();
            roomBedList.add(roomBed);
        }
        roomBedRepository.saveAll(roomBedList);
        if(request.getImages() != null){
            cloudinaryService.uploadImageList(request.getImages())
                    .thenAccept(imageUrl -> {
                        updateRoomImageUrl(roomRank.getId(), imageUrl,roomBedList);
                    })
                    .exceptionally(ex -> {
                        System.out.println("Có lỗi xảy ra trong quá trình upload: " + ex.getMessage());
                        return null;
                    });
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .message("Successfully created a new room rank")
                                .data(roomRank)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> editRoomRank(EditRankRoomRequest request, Principal principal) throws IOException {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if(user == null) throw new AppException(ErrorCode.NOT_FOUND);
        List<Amenity> amenities = amenityRepository.findAllById(request.getAmenityId());
        RoomRank roomRankCurrent = roomRankRepository.findById(request.getRankId()).orElse(null);
        roomRankCurrent.setName(request.getName());
        roomRankCurrent.setDescription(request.getDescription());
        roomRankCurrent.setArea(request.getArea());
        roomRankCurrent.setAmenity(amenities);
        roomRankCurrent.setUpdateAt(LocalDateTime.now());
        roomRankCurrent.setUpdateBy(user.getEmail());
        roomRankRepository.save(roomRankCurrent);
        List<RoomBed> roomBedList = new ArrayList<>();
        for(String bedString : request.getBed()){
            String[] split = bedString.split(":");
            Bed bed = bedRepository.findById(Integer.parseInt(split[0])).get();
            RoomBed roomBed = RoomBed.builder()
                    .bed(bed)
                    .rank(roomRankCurrent)
                    .quantity(Integer.parseInt(split[1]))
                    .build();
            roomBedList.add(roomBed);
        }
        for(Image image : roomRankCurrent.getImages()){
            if(!request.getImageId().contains(image.getId())){
                System.out.println(image.getId());
                imageRepository.delete(image);
            }
        }
        if(!roomRankCurrent.getRoomBeds().isEmpty()){
            roomBedRepository.deleteAll(roomRankCurrent.getRoomBeds());
            System.out.println("delete bed");
        }
        roomBedRepository.saveAll(roomBedList);
        if(request.getImages() != null){
            cloudinaryService.uploadImageList(request.getImages())
                    .thenAccept(imageUrl -> {
                        updateRoomImageUrl(roomRankCurrent.getId(), imageUrl,roomBedList);
                    })
                    .exceptionally(ex -> {
                        System.out.println("Có lỗi xảy ra trong quá trình upload: " + ex.getMessage());
                        return null;
                    });
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully edit a new room rank")
                                .data(roomRankCurrent)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> active(int rankId) {
        RoomRank roomRank = roomRankRepository.findById(rankId).orElse(null);
        if(roomRank == null) throw new AppException(ErrorCode.NOT_FOUND);
        if(roomRank.getActive() == null) roomRank.setActive(true);
        else{
            roomRank.setActive(!roomRank.getActive());
        }
        roomRankRepository.save(roomRank);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully active/inactive a new room rank")
                                .build()
                );
    }
    public void updateRoomImageUrl(int roomId, List<String> imageUrl, List<RoomBed> roomBedList) {
        RoomRank room = roomRankRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Phòng không tồn tại"));
        List<Image> images = new ArrayList<>();
        for(String url : imageUrl){
            Image image = Image.builder()
                    .rank(room)
                    .path(url)
                    .build();
            images.add(image);
        }
//        if(!room.getImages().isEmpty()){
//            imageRepository.deleteAll(room.getImages());
//            System.out.println("delete image");
//        }

        imageRepository.saveAll(images);
        room.setRoomBeds(roomBedList);
        room.setImages(images);
        roomRankRepository.save(room);
    }
}
