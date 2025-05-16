package com.hotel.booking.service.Impl;

import com.hotel.booking.dto.ApiResponse;
import com.hotel.booking.dto.category.CategoryDto;
import com.hotel.booking.dto.serviceHotel.*;
import com.hotel.booking.exception.AppException;
import com.hotel.booking.exception.ErrorCode;
import com.hotel.booking.model.*;
import com.hotel.booking.repository.*;
import com.hotel.booking.service.CloudinaryService;
import com.hotel.booking.service.PaymentService;
import com.hotel.booking.service.ServiceHotelService;
import com.hotel.booking.service.ZaloPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IServiceHotelService implements ServiceHotelService {
    final private ServiceHotelRepository serviceHotelRepository;
    final private CloudinaryService cloudinaryService;
    final private CategoryRepository categoryRepository;
    final private RoomServiceModelRepository roomServiceModelRepository;
    final private UserServiceHotelRepository userServiceHotelRepository;
    final private ZaloPayService zaloPayService;
    private final RoomRepository roomRepository;
    private final BookingRoomRepository bookingRoomRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRoomRepository serviceRoomRepository;
    private final BillRepository billRepository;
    private final PaymentService paymentService;

    @Override
    public ResponseEntity<?> addServiceHotel(CreateServiceHotel serviceHotel, Principal principal) {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        ServiceHotel service = ServiceHotel.builder()
                .name(serviceHotel.getName())
                .location(serviceHotel.getLocation())
                .capacity(serviceHotel.getCapacity())
                .openTime(serviceHotel.getStartTime())
                .price(serviceHotel.getPrice())
                .closeTime(serviceHotel.getEndTime())
                .description(serviceHotel.getDescription())
                .active(true)
                .category(categoryRepository.findById(serviceHotel.getCategoryId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND)))
                .createAt(LocalDateTime.now())
                .createBy(user.getEmail())
                .build();
        serviceHotelRepository.save(service);
        if(serviceHotel.getImage() != null){
            cloudinaryService.uploadImage(serviceHotel.getImage())
                    .thenAccept(imageUrl -> {
                        service.setImage(imageUrl);
                        serviceHotelRepository.save(service);
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
                                .data(service)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> updateServiceHotel(UpdateServiceHotel serviceHotel, Principal principal) {
        ServiceHotel service = serviceHotelRepository.findById(serviceHotel.getServiceHotelId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        service.setName(serviceHotel.getName());
        service.setLocation(serviceHotel.getLocation());
        service.setCapacity(serviceHotel.getCapacity());
        service.setOpenTime(serviceHotel.getStartTime());
        service.setPrice(serviceHotel.getPrice());
        service.setCloseTime(serviceHotel.getEndTime());
        service.setDescription(serviceHotel.getDescription());
        service.setCategory(categoryRepository.findById(serviceHotel.getCategoryId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND)));
        service.setUpdateAt(LocalDateTime.now());
        service.setUpdateBy(user.getEmail());
        serviceHotelRepository.save(service);
        if(serviceHotel.getImage() != null){
            cloudinaryService.uploadImage(serviceHotel.getImage())
                    .thenAccept(imageUrl -> {
                        service.setImage(imageUrl);
                        serviceHotelRepository.save(service);
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
                                .message("Successfully update a new service hotel")
                                .data(service)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> deleteServiceHotel(Integer id) {
        ServiceHotel serviceHotel = serviceHotelRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        boolean status = !serviceHotel.getActive();
        serviceHotel.setActive(status);
        serviceHotelRepository.save(serviceHotel);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully active/inactive a service hotel")
                                .data(serviceHotel)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> getServiceHotelById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<?> getAllServiceHotels() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully get list service hotel")
                                .data(serviceHotelRepository.findAll())
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> getAllCategory() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully get all category service hotel")
                                .data(categoryRepository.findAll())
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> getByCategory() {
        List<ServiceCategory> categoryList = categoryRepository.findAll();
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for(ServiceCategory category: categoryList){
            if(category.getActive()){
                List<ServiceDto> serviceDtoList = new ArrayList<>();
                for(ServiceHotel serviceHotel: serviceHotelRepository.findServiceHotelsByCategoryAndActive(category,true)){
                    ServiceDto serviceDto = ServiceDto.builder()
                            .id(serviceHotel.getId())
                            .name(serviceHotel.getName())
                            .description(serviceHotel.getDescription())
                            .image(serviceHotel.getImage())
                            .endTime(serviceHotel.getCloseTime())
                            .startTime(serviceHotel.getOpenTime())
                            .capacity(serviceHotel.getCapacity())
                            .location(serviceHotel.getLocation())
                            .price(serviceHotel.getPrice())
                            .build();
                    serviceDtoList.add(serviceDto);
                }
                CategoryDto categoryDto = CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .serviceHotelList(serviceDtoList)
                        .build();
                categoryDtoList.add(categoryDto);
            }

        }
        CategoryDto categoryServiceRoom = CategoryDto.builder()
                .id(3)
                .name("Dịch vụ khác")
                .description("Các dịch vụ phòng khách sạn")
                .serviceHotelList(roomServiceModelRepository
                        .findAll()
                        .stream()
                        .map(roomServiceModel -> ServiceDto.builder()
                                .id(roomServiceModel.getId())
                                .name(roomServiceModel.getName())
                                .description(roomServiceModel.getDescription())
                                .image(roomServiceModel.getIcon())
                                .build())
                        .toList())
                .build();
        categoryDtoList.add(categoryServiceRoom);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully get service hotels by category")
                                .data(categoryDtoList)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> serviceHotelById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<?> bookingServiceHotel(BookingServiceHotel bookingServiceHotel, Principal principal) throws Exception {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        ServiceHotel serviceHotel = serviceHotelRepository
                .findById(
                        bookingServiceHotel
                                .getServiceHotelId()
                ).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        UserServiceHotel userServiceHotel = UserServiceHotel.builder()
                .user(user)
                .serviceHotel(serviceHotel)
                .note(bookingServiceHotel.getNote())
                .status("PROCESSING")
                .build();
        userServiceHotelRepository.save(userServiceHotel);
        Map<String,Object> kq = zaloPayService.createPayment(
                "booking service hotel",
                Long.valueOf(serviceHotel.getPrice()),
                Long.valueOf(userServiceHotel.getId())
        );
        kq.put("payment_id",userServiceHotel.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully booking")
                                .data(kq)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> checkBookingStatus(Long bookingId, String transId) throws Exception {
        UserServiceHotel userServiceHotel = userServiceHotelRepository.findById(bookingId).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        Map<String,Object> kq = zaloPayService.getStatusByApptransid(transId);
        if(kq.get("returncode") != null && (Integer) kq.get("returncode") != 1){
            userServiceHotel.setStatus("FAIL");
            userServiceHotelRepository.save(userServiceHotel);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ApiResponse.builder()
                                    .statusCode(400)
                                    .message("PAYMENT_FAIL")
                                    .description("Thanh toán thất bại.")
                                    .data(userServiceHotel)
                                    .build()
                    );
        }
        userServiceHotel.setStatus("SUCCESS");
        userServiceHotelRepository.save(userServiceHotel);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(200)
                                .message("PAYMENT_SUCCESS")
                                .description("Thanh toán thành công.")
                                .data(userServiceHotel)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> serviceRoomDetail(int serviceRoomId, Principal principal) {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        List<BookingRoom> bookingRoomList = roomRepository.findRoomIdsByUserIdAndServiceId(user,serviceRoomId);
        if(bookingRoomList.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ApiResponse.builder()
                                    .statusCode(400)
                                    .message("SERVICE_ROOM-DETAIL_SUCCESS")
                                    .description("Bạn chưa đặt phòng nào có thể sử dụng dịch vụ này. Vui lòng đặt mới")
                                    .build()
                    );
        }
        List<BookedServiceRoom> bookedServiceRoomList = new ArrayList<>();
        RoomServiceModel roomServiceModel = roomServiceModelRepository
                .findById(serviceRoomId)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));

        for (BookingRoom bookingRoom : bookingRoomList) {
            Room room = roomRepository.findRoomDetailById(bookingRoom.getRoomDetail().getId());
            ServiceRoom serviceRoom = serviceRoomRepository
                    .findByRoomAndService(room, roomServiceModel);

            BookedServiceRoom bookedServiceRoom = BookedServiceRoom.builder()
                    .bookingRoomId(bookingRoom.getId())
                    .roomName(room.getName())
                    .adult(bookingRoom.getSumAdult())
                    .child(bookingRoom.getSumChildren())
                    .checkInTime(bookingRoom.getCheckin())
                    .checkOutTime(bookingRoom.getCheckout())
                    .priceService(serviceRoom.getPrice())
                    .build();
            bookedServiceRoomList.add(bookedServiceRoom);
        }

        ServiceRoomDetail serviceRoomDetail = ServiceRoomDetail.builder()
                .serviceRoomId(serviceRoomId)
                .serviceRoomDescription(roomServiceModel.getDescription())
                .serviceRoomName(roomServiceModel.getName())
                .customerName(user.getUsername())
                .bookedServiceRoomList(bookedServiceRoomList)
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(200)
                                .message("SERVICE_ROOM_DETAIL_SUCCESS")
                                .description("Hiển thị danh sách phòng có thể chọn thành công.")
                                .data(serviceRoomDetail)
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> bookingServiceRoom(BookingServiceRoom bookingServiceRoom, Principal principal) throws Exception {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        BookingRoom bookingRoom = bookingRoomRepository.findById(bookingServiceRoom.getBookingId())
                .orElseThrow(()->new AppException(ErrorCode.BOOKING_ROOM_NOT_FOUND));
        RoomServiceModel roomServiceModel = roomServiceModelRepository.
                findById(bookingServiceRoom.getServiceRoomId())
                .orElseThrow(()->new AppException(ErrorCode.SERVICE_NOT_FOUND));
        Room room = roomRepository.findRoomDetailById(bookingRoom.getRoomDetail().getId());
        ServiceRoom serviceRoom = serviceRoomRepository.findByRoomAndService(room, roomServiceModel);
        String newServiceId = "";
        if(!bookingRoom.getServiceId().equals("0")) {
            if(bookingRoom.getServiceId().length() > 1){
                String[] bookedServiceId = bookingRoom.getServiceId().split(",");
                for(String s : bookedServiceId){
                    if(s.equals(String.valueOf(bookingServiceRoom.getServiceRoomId()))){
                        return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(
                                        ApiResponse.builder()
                                                .statusCode(403)
                                                .message("SERVICE_ROOM_BOOKED_FAIL")
                                                .description("Dich vụ này đã có trong phòng của bạn rồi.")
                                                .build()
                                );
                    }
                }

            } else{
                if(String.valueOf(bookingServiceRoom.getServiceRoomId()).equals(bookingRoom.getServiceId())){
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(
                                    ApiResponse.builder()
                                            .statusCode(403)
                                            .message("SERVICE_ROOM_BOOKED_FAIL")
                                            .description("Dich vụ này đã có trong phòng của bạn rồi.")
                                            .build()
                            );
                }
            }
            newServiceId = bookingRoom.getServiceId() + ","+String.valueOf(bookingServiceRoom.getServiceRoomId());
            //bookingRoom.setServiceId(newServiceId);

        }else{
            newServiceId = String.valueOf(bookingServiceRoom.getServiceRoomId());
        }
//        bookingRoom.setServiceId(newServiceId);
//        int newPrice = bookingRoom.getPrice() + serviceRoom.getPrice();
//        int oldPrice = bookingRoom.getPrice();
//        bookingRoom.setPrice(newPrice);
//        Booking booking = bookingRoom.getBooking();
//        int newSumPrice = booking.getSumPrice() - oldPrice + newPrice;
//        booking.setSumPrice(newSumPrice);
//        booking.setNote(booking.getNote() + "/" + bookingServiceRoom.getNote());
        String note = bookingRoom.getBooking().getNote();
        int servicePrice = serviceRoom.getPrice();
        Map<String,Object> kq = zaloPayService.createPayment(
                "booking service room",
                Long.valueOf(serviceRoom.getPrice()),
                Long.valueOf(bookingRoom.getId())
        );
        Bill bill = Bill.builder()
                .booking(bookingRoom.getBooking())
                .paymentDate(String.valueOf(serviceRoom.getId()))
                .paymentAmount(String.valueOf(serviceRoom.getPrice()))
                .note("SERVICE_ROOM")
                .status("SERVICE_PROCESSING")
                .createAt(LocalDateTime.now())
                .transId(kq.get("apptransid").toString())
                .build();
        billRepository.save(bill);
        kq.put("payment_id",bill.getId());
        paymentService.checkPaymentAsync(bookingRoom, bill, newServiceId, servicePrice, note);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(200)
                                .message("SERVICE_ROOM_BOOKED_SUCCESS")
                                .description("Đặt dịnh vụ thành công")
                                .data(kq)
                                .build()
                );
    }
    @Async
    protected void checkPaymentAsync(Booking booking, BookingRoom bookingRoom, Bill bill) throws Exception {
        long startTime = System.currentTimeMillis();
        boolean isPaid = false;

        while (System.currentTimeMillis() - startTime < 15 * 60 * 1000) { // Chạy trong 15 phút
            Map<String, Object> paymentStatus = zaloPayService.getStatusByApptransid(bill.getTransId());

            if ((Integer) paymentStatus.get("returncode") == 1) { // Nếu thanh toán thành công
                bill.setStatus("SUCCESS");
                billRepository.save(bill);

                bookingRepository.save(booking);
                bookingRoomRepository.save(bookingRoom);

                isPaid = true;
                break; // Dừng kiểm tra ngay khi thanh toán thành công
            }

            try {
                Thread.sleep(120000); // Chờ 2 phút trước khi kiểm tra lại
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (!isPaid) {
            bill.setStatus("CANCELED");
            billRepository.save(bill);
        }
    }


    @Override
    public ResponseEntity<?> checkBookingRoomStatus(int paymentId, String transId) throws Exception {

        Map<String,Object> kq = zaloPayService.getStatusByApptransid(transId);
        Bill bill = billRepository.findById(paymentId).get();
        if(kq.get("returncode") != null && (Integer) kq.get("returncode") != 1){

            bill.setStatus("CANCELED");
            billRepository.save(bill);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ApiResponse.builder()
                                    .statusCode(400)
                                    .message("PAYMENT_FAIL")
                                    .description("Thanh toán thất bại.")
                                    .build()
                    );
        }
        bill.setStatus("SUCCESS");
        billRepository.save(bill);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(200)
                                .message("PAYMENT_SUCCESS")
                                .description("Thanh toán thành công.")
                                .build()
                );
    }

    @Override
    public void checkPaymentsAsync(Booking booking, BookingRoom bookingRoom, Bill bill) {
        
    }
}
