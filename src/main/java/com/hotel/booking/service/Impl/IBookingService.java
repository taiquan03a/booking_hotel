package com.hotel.booking.service.Impl;

import com.hotel.booking.dto.ApiResponse;
import com.hotel.booking.dto.booking.BookingRoomResponse;
import com.hotel.booking.dto.booking.CartResponse;
import com.hotel.booking.dto.booking.CreateCart;
import com.hotel.booking.exception.AppException;
import com.hotel.booking.exception.ErrorCode;
import com.hotel.booking.model.*;
import com.hotel.booking.model.Enum.BookingStatusEnum;
import com.hotel.booking.model.Enum.PolicyTypeEnum;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.BookingRoomRepository;
import com.hotel.booking.repository.RoomDetailRepository;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.*;

@Service
public class IBookingService implements BookingService {

    private final RoomDetailRepository roomDetailRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final BookingRoomRepository bookingRoomRepository;

    public IBookingService(RoomDetailRepository roomDetailRepository, RoomRepository roomRepository, BookingRepository bookingRepository, BookingRoomRepository bookingRoomRepository) {
        this.roomDetailRepository = roomDetailRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.bookingRoomRepository = bookingRoomRepository;
    }

    @Override
    public ResponseEntity<?> addToCart(CreateCart createCart, Principal principal) {
        User user = (principal != null) ? (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal() : null;
        if(user == null)
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            ApiResponse.builder()
                                    .statusCode(HttpStatus.FORBIDDEN.value())
                                    .message("USER_NOT_FOUND")
                                    .description("Vui lòng đăng nhập trước khi đặt phòng.")
                                    .build()
                    );
        RoomDetail roomDetail = roomDetailRepository.findById(createCart.getRoomNumberId()).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        if(!roomDetail.getBookingRooms().isEmpty()){
            for(BookingRoom bookingRoom : roomDetail.getBookingRooms()){
                if(
                        bookingRoom.getStatus().equals(String.valueOf(BookingStatusEnum.BOOKED)) &&
                        bookingRoom.getCheckout().isAfter(createCart.getCheckinDate().atTime(14,0))
                )
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(
                                    ApiResponse.builder()
                                            .statusCode(HttpStatus.BAD_REQUEST.value())
                                            .message("ROOM_BOOKED")
                                            .description("Phòng đã được đặt.")
                                            .build()
                            );
            }
        }
        Room room = roomRepository.findRoomsByRoomDetail(roomDetail);
        Map<String, String> policyMap = new HashMap<>();
        for (Policy policy : room.getPolicies()) {
            policyMap.put(policy.getType().getType(), policy.getContent());
        }
        int adultPolicy = Integer.parseInt(policyMap.get(String.valueOf(PolicyTypeEnum.ADULT)));
        int childPolicy = Integer.parseInt(policyMap.get(String.valueOf(PolicyTypeEnum.CHILD)));
        if (createCart.getAdults() + createCart.getChildren() > room.getAdultMax())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ApiResponse.builder()
                                    .statusCode(407)
                                    .message("MAX_ADULT")
                                    .description("Số người vượt quá giới hạn vui lòng chọn lại.")
                                    .build()
                    );
        int adultPlus = 0;
        int childPlus = 0;
        if (createCart.getAdults() > room.getAdultNumber()) {
            adultPlus = (createCart.getAdults() - room.getAdultNumber()) * adultPolicy;
            childPlus = createCart.getChildren() * childPolicy;
        }else{
            int x = room.getAdultNumber() - createCart.getAdults();
            childPlus =(createCart.getChildren() - x) * childPolicy;
        }
        List<Booking> bookingUser = bookingRepository.findByUser(user);
        Booking bookingCart = bookingUser.stream()
                .filter(booking -> booking.getStatus() != null && booking.getStatus().equals(String.valueOf(BookingStatusEnum.CART)))
                .findFirst()
                .orElse(null);
        if(bookingCart == null){
            bookingCart = new Booking();
            bookingCart.setUser(user);
            bookingCart.setStatus(String.valueOf(BookingStatusEnum.CART));
            bookingRepository.save(bookingCart);
        }else{
            List<BookingRoom> roomDetailCart = bookingCart.getBookingRooms();
            for(BookingRoom roomCart : roomDetailCart){
                if(roomCart.getRoomDetail().getId() == createCart.getRoomNumberId()){
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(
                                    ApiResponse.builder()
                                            .statusCode(406)
                                            .message("ROOM_CARTED")
                                            .description("Phòng này đã được bạn chọn rồi vui lòng chọn phòng khác.")
                                            .build()
                            );
                }
            }
        }
        BookingRoom bookingRoom = BookingRoom.builder()
                .sumAdult(createCart.getAdults())
                .sumChildren(createCart.getChildren())
                .sumInfant(createCart.getInfants())
                .checkin(createCart.getCheckinDate().atTime(14,0))
                .checkout(createCart.getCheckoutDate().atTime(12,0))
                .status(String.valueOf(BookingStatusEnum.CART))
                .statusTime(LocalDateTime.now())
                .adultSurcharge(adultPlus)
                .childSurcharge(childPlus)
                .roomDetail(roomDetail)
                .booking(bookingCart)
                .price(room.getPrice() + adultPlus + childPlus)
                .build();
        bookingRoomRepository.save(bookingRoom);
        bookingCart.setSumRoom(bookingCart.getSumRoom() + 1);
        bookingCart.setSumPrice(bookingCart.getSumPrice() + bookingRoom.getPrice());
        bookingRepository.save(bookingCart);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Chọn phòng " + roomDetail.getRoomNumber() + " thành công.")
                                .data(bookingRoom)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> booking(Principal principal) {
        User user = (principal != null) ? (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal() : null;
        Booking booking = bookingRepository.findByUser(user).stream().filter(book -> book.getStatus().equals(String.valueOf(BookingStatusEnum.CART))).findFirst().get();
        List<BookingRoomResponse> roomCart = new ArrayList<>();
        int roomPrice = 0, totalPrice = 0;
        for(BookingRoom bookingRoom: booking.getBookingRooms()){
            RoomDetail detail = bookingRoom.getRoomDetail();
            roomPrice += detail.getRoom().getPrice();
            totalPrice += bookingRoom.getPrice();
            BookingRoomResponse bookingRoomResponse = BookingRoomResponse.builder()
                    .roomNumber(detail.getRoomNumber())
                    .roomCode(detail.getRoomCode())
                    .roomName(detail.getRoom().getName())
                    .roomType(detail.getRoom().getRoomRank().getName())
                    .checkin(String.valueOf(bookingRoom.getCheckin()))
                    .checkout(String.valueOf(bookingRoom.getCheckout()))
                    .price(bookingRoom.getPrice())
                    .build();
            roomCart.add(bookingRoomResponse);
        }
        CartResponse response = CartResponse.builder()
                .roomPrice(roomPrice)
                .totalPrice(totalPrice)
                .policyPrice(totalPrice - roomPrice)
                .roomCart(roomCart)
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Cart user ->" + user.getEmail())
                                .data(response)
                                .build()
                );
    }
}
