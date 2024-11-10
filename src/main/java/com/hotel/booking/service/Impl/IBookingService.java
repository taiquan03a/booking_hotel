package com.hotel.booking.service.Impl;

import com.hotel.booking.dto.ApiResponse;
import com.hotel.booking.dto.booking.*;
import com.hotel.booking.dto.roomService.RoomServiceResponse;
import com.hotel.booking.dto.roomService.ServiceRoomSelect;
import com.hotel.booking.exception.AppException;
import com.hotel.booking.exception.ErrorCode;
import com.hotel.booking.mapping.PolicyMapper;
import com.hotel.booking.mapping.RoomServiceMapper;
import com.hotel.booking.model.*;
import com.hotel.booking.model.Enum.BookingStatusEnum;
import com.hotel.booking.model.Enum.PolicyTypeEnum;
import com.hotel.booking.repository.*;
import com.hotel.booking.service.BookingService;
import com.hotel.booking.service.ZaloPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IBookingService implements BookingService {

    private final RoomDetailRepository roomDetailRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final BookingRoomRepository bookingRoomRepository;
    private final ServiceRoomRepository serviceRoomRepository;
    private final RoomServiceModelRepository roomServiceModelRepository;
    private final UserRepository userRepository;
    private final ZaloPayService zaloPayService;
    private final BillRepository billRepository;

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
            bookingCart.setSumRoom(0);
            bookingCart.setSumPrice(0);
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
                .serviceId("0")
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
        Booking booking = bookingRepository.findByUser(user)
                .stream().filter(book -> book.getStatus().equals(String.valueOf(BookingStatusEnum.CART))).findFirst().get();
        List<BookingRoomResponse> roomCart = new ArrayList<>();
        int roomPrice = 0, totalPrice = 0,policyPrice = 0;
        for(BookingRoom bookingRoom: booking.getBookingRooms()){
            RoomDetail detail = bookingRoom.getRoomDetail();
            roomPrice += detail.getRoom().getPrice();
            totalPrice += bookingRoom.getPrice();
            policyPrice += bookingRoom.getAdultSurcharge() + bookingRoom.getChildSurcharge();
            BookingRoomResponse bookingRoomResponse = BookingRoomResponse.builder()
                    .bookingRoomId(bookingRoom.getId())
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
                .policyPrice(policyPrice)
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

    @Override
    public ResponseEntity<?> removeFromCart(Principal principal, int bookingRoomId) {
        User user = (principal != null) ? (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal() : null;
        Booking booking = bookingRepository.findByUser(user)
                .stream().filter(book -> book.getStatus().equals(String.valueOf(BookingStatusEnum.CART))).findFirst().get();
        BookingRoom roomCarted = bookingRoomRepository.findById(bookingRoomId).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        booking.setSumRoom(booking.getSumRoom() - 1);
        booking.setSumPrice(booking.getSumPrice() - roomCarted.getPrice());
        bookingRepository.save(booking);
        bookingRoomRepository.delete(roomCarted);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Xóa thành công phòng " + roomCarted.getRoomDetail().getRoomNumber())
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> checkout(Principal principal) {
        User user = (principal != null) ? (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal() : null;
        Booking booking = bookingRepository.findByUser(user)
                .stream().filter(book -> book.getStatus().equals(String.valueOf(BookingStatusEnum.CART))).findFirst().get();
        List<BookingRoomDetail> bookingDetails = new ArrayList<>();
        int totalPolicyPrice= 0,totalBookingPrice = 0, totalRoomPrice = 0;
        for(BookingRoom bookingRoom: booking.getBookingRooms()){
            RoomDetail detail = bookingRoom.getRoomDetail();
            List<ServiceRoomSelect> serviceSelect = new ArrayList<>();
            List<Integer> serviceSelectedId = bookingRoom.getServiceId().equals("0")
                    ? List.of()
                    : Arrays.stream(bookingRoom.getServiceId().split(","))
                    .map(Integer::parseInt)
                    .toList();

            for(RoomServiceModel serviceModel : detail.getRoom().getService()){
                ServiceRoom serviceRoom = serviceRoomRepository.findByRoomAndService(detail.getRoom(),serviceModel);
                boolean exists = false;
                for(int it : serviceSelectedId){
                    if(it == serviceModel.getId()) {
                        exists = true;
                        break;
                    }
                }
                if(serviceRoom != null){
                    ServiceRoomSelect select = ServiceRoomSelect.builder()
                            .id(serviceModel.getId())
                            .name(serviceModel.getName())
                            .selected(exists)
                            .price(serviceRoom.getPrice())
                            .build();
                    serviceSelect.add(select);
                }
            }
            BookingRoomDetail bookingCarted = BookingRoomDetail.builder()
                    .bookingRoomId(bookingRoom.getId())
                    .roomNumber(detail.getRoomNumber())
                    .roomCode(detail.getRoomCode())
                    .roomName(detail.getRoom().getName())
                    .roomType(detail.getRoom().getRoomRank().getName())
                    .image(detail.getRoom().getRoomRank().getImages().get(0).getPath())
                    .checkIn(String.valueOf(bookingRoom.getCheckin()))
                    .checkOut(String.valueOf(bookingRoom.getCheckout()))
                    .adults(bookingRoom.getSumAdult())
                    .children(bookingRoom.getSumChildren())
                    .infant(bookingRoom.getSumInfant())
                    .adultSurcharge(bookingRoom.getAdultSurcharge())
                    .childSurcharge(bookingRoom.getChildSurcharge())
                    .roomPrice(detail.getRoom().getPrice())
                    .totalPrice(bookingRoom.getPrice())
                    .policyList(PolicyMapper.INSTANCE.toResponseList(detail.getRoom().getPolicies()))
                    .serviceList(serviceSelect)
                    .build();

            bookingDetails.add(bookingCarted);
            totalPolicyPrice += bookingCarted.getAdultSurcharge() + bookingCarted.getChildSurcharge();
            totalRoomPrice += bookingCarted.getRoomPrice();
            totalBookingPrice += bookingCarted.getTotalPrice();
        }
        CartDetailResponse cartDetail = CartDetailResponse.builder()
                .totalRoomBooking(booking.getBookingRooms().size())
                .totalRoomPrice(totalRoomPrice)
                .totalBookingPrice(totalBookingPrice)
                .totalPolicyPrice(totalPolicyPrice)
                .bookingRoomDetails(bookingDetails)
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Booking cart OK")
                                .data(cartDetail)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> editCart(Principal principal, int adult, int child,int infant, String serviceId,int bookingRoomId) {
        User user = (principal != null) ? (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal() : null;
        Booking booking = bookingRepository.findByUser(user)
                .stream().filter(book -> book.getStatus().equals(String.valueOf(BookingStatusEnum.CART))).findFirst().get();
        BookingRoom bookingRoom = bookingRoomRepository.findById(bookingRoomId).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        if(!bookingRoom.getStatus().equals(String.valueOf(BookingStatusEnum.CART)))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ApiResponse.builder()
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .message("ROOM_NOT_CART")
                                    .description("Phòng đang không thuộc giỏ.")
                                    .build()
                    );
        if(adult != -1) bookingRoom.setSumAdult(adult);
        if(child != -1) bookingRoom.setSumChildren(child);
        if(infant != -1) bookingRoom.setSumInfant(infant);
        if(!serviceId.equals("-1")) bookingRoom.setServiceId(serviceId);
        Room room = roomRepository.findRoomsByRoomDetail(bookingRoom.getRoomDetail());
        Map<String, String> policyMap = new HashMap<>();
        for (Policy policy : room.getPolicies()) {
            policyMap.put(policy.getType().getType(), policy.getContent());
        }
        int adultPolicy = Integer.parseInt(policyMap.get(String.valueOf(PolicyTypeEnum.ADULT)));
        int childPolicy = Integer.parseInt(policyMap.get(String.valueOf(PolicyTypeEnum.CHILD)));
        if (bookingRoom.getSumAdult() + bookingRoom.getSumChildren() > room.getAdultMax())
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
        if (bookingRoom.getSumAdult() > room.getAdultNumber()) {
            adultPlus = (bookingRoom.getSumAdult() - room.getAdultNumber()) * adultPolicy;
            childPlus = bookingRoom.getSumChildren() * childPolicy;
        }else{
            int x = room.getAdultNumber() - bookingRoom.getSumAdult();
            if(bookingRoom.getSumChildren() >= x)
                childPlus =(bookingRoom.getSumChildren() - x) * childPolicy;
        }
        int bookingRoomPriceOld = bookingRoom.getPrice();
        bookingRoom.setAdultSurcharge(adultPlus);
        bookingRoom.setChildSurcharge(childPlus);
        bookingRoom.setPrice(room.getPrice() + adultPlus + childPlus);
        booking.setSumPrice(bookingRoom.getPrice() - bookingRoomPriceOld + bookingRoom.getPrice());
        bookingRoomRepository.save(bookingRoom);
        bookingRepository.save(booking);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(200)
                                .message("Thanh cong")
                                .data("check")
                                .build()
                );
    }

    @Override
    public Map<String, Object> payment(Principal principal, Long customerId) throws Exception {
        User customer = userRepository.findById(customerId).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        User user = (principal != null) ? (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal() : null;
        Booking booking = bookingRepository.findByUser(user)
                .stream().filter(book -> book.getStatus().equals(String.valueOf(BookingStatusEnum.CART))).findFirst().get();
        booking.setUser(customer);
        booking.setStatus(String.valueOf(BookingStatusEnum.BOOKED));
        booking.getBookingRooms().forEach(
                bookingRoom -> bookingRoom.setStatus(String.valueOf(BookingStatusEnum.BOOKED))
        );
        bookingRepository.save(booking);
        Map<String,Object> kq = zaloPayService.createPayment("booking",Long.valueOf(booking.getSumPrice()), Long.valueOf(booking.getId()));
        Bill bill = Bill.builder()
                .booking(booking)
                .paymentAmount(String.valueOf(booking.getSumPrice()))
                .status("PROCESSING")
                .transId(kq.get("apptransid").toString())
                .createAt(LocalDateTime.now())
                .build();
        billRepository.save(bill);
        kq.put("paymentId",bill.getId());
        return kq;
    }

    @Override
    public ResponseEntity<?> checkBill(String transId,int paymentId) throws Exception {
        Bill bill = billRepository.findById(paymentId).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        Booking booking = billRepository.findBookingByBillId(paymentId);
        Map<String,Object> kq = zaloPayService.getStatusByApptransid(transId);
        if(!kq.get("returncode").equals("1")){
            bill.setStatus("FAIL");
            billRepository.save(bill);
            booking.setStatus(String.valueOf(BookingStatusEnum.CANCELED));
            booking.getBookingRooms().forEach(
                    bookingRoom -> bookingRoom.setStatus(String.valueOf(BookingStatusEnum.CANCELED))
            );
            bookingRepository.save(booking);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ApiResponse.builder()
                                    .statusCode(400)
                                    .message("PAYMENT_FAIL")
                                    .description("Thanh toán thất bại.")
                                    .data(getBillDetail(booking))
                                    .build()
                    );
        }
        bill.setStatus("SUCCESS");
        billRepository.save(bill);
        booking.setStatus(String.valueOf(BookingStatusEnum.BOOKED));
        booking.getBookingRooms().forEach(
                bookingRoom -> bookingRoom.setStatus(String.valueOf(BookingStatusEnum.BOOKED))
        );
        bookingRepository.save(booking);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(200)
                                .message("PAYMENT_SUCCESS")
                                .description("Thanh toán thành công.")
                                .data(getBillDetail(booking))
                                .build()
                );

    }
    private CartDetailResponse getBillDetail(Booking booking) {
        List<BookingRoomDetail> bookingDetails = new ArrayList<>();
        int totalPolicyPrice= 0,totalBookingPrice = 0, totalRoomPrice = 0;
        for(BookingRoom bookingRoom: booking.getBookingRooms()){
            RoomDetail detail = bookingRoom.getRoomDetail();
            List<ServiceRoomSelect> serviceSelect = new ArrayList<>();
            List<Integer> serviceSelectedId = bookingRoom.getServiceId().equals("0")
                    ? List.of()
                    : Arrays.stream(bookingRoom.getServiceId().split(","))
                    .map(Integer::parseInt)
                    .toList();

            for(RoomServiceModel serviceModel : detail.getRoom().getService()){
                ServiceRoom serviceRoom = serviceRoomRepository.findByRoomAndService(detail.getRoom(),serviceModel);
                boolean exists = false;
                for(int it : serviceSelectedId){
                    if(it == serviceModel.getId()) {
                        exists = true;
                        break;
                    }
                }
                if(serviceRoom != null){
                    ServiceRoomSelect select = ServiceRoomSelect.builder()
                            .id(serviceModel.getId())
                            .name(serviceModel.getName())
                            .selected(exists)
                            .price(serviceRoom.getPrice())
                            .build();
                    serviceSelect.add(select);
                }
            }
            BookingRoomDetail bookingCarted = BookingRoomDetail.builder()
                    .bookingRoomId(bookingRoom.getId())
                    .roomNumber(detail.getRoomNumber())
                    .roomCode(detail.getRoomCode())
                    .roomName(detail.getRoom().getName())
                    .roomType(detail.getRoom().getRoomRank().getName())
                    .image(detail.getRoom().getRoomRank().getImages().get(0).getPath())
                    .checkIn(String.valueOf(bookingRoom.getCheckin()))
                    .checkOut(String.valueOf(bookingRoom.getCheckout()))
                    .adults(bookingRoom.getSumAdult())
                    .children(bookingRoom.getSumChildren())
                    .infant(bookingRoom.getSumInfant())
                    .adultSurcharge(bookingRoom.getAdultSurcharge())
                    .childSurcharge(bookingRoom.getChildSurcharge())
                    .roomPrice(detail.getRoom().getPrice())
                    .totalPrice(bookingRoom.getPrice())
                    .policyList(PolicyMapper.INSTANCE.toResponseList(detail.getRoom().getPolicies()))
                    .serviceList(serviceSelect)
                    .build();

            bookingDetails.add(bookingCarted);
            totalPolicyPrice += bookingCarted.getAdultSurcharge() + bookingCarted.getChildSurcharge();
            totalRoomPrice += bookingCarted.getRoomPrice();
            totalBookingPrice += bookingCarted.getTotalPrice();
        }
        return CartDetailResponse.builder()
                .totalRoomBooking(booking.getBookingRooms().size())
                .totalRoomPrice(totalRoomPrice)
                .totalBookingPrice(totalBookingPrice)
                .totalPolicyPrice(totalPolicyPrice)
                .bookingRoomDetails(bookingDetails)
                .build();
    }
}
