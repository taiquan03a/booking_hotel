package com.hotel.booking.service.Impl;

import com.hotel.booking.dto.ApiResponse;
import com.hotel.booking.dto.category.CategoryDto;
import com.hotel.booking.dto.serviceHotel.CreateServiceHotel;
import com.hotel.booking.dto.serviceHotel.ServiceDto;
import com.hotel.booking.dto.serviceHotel.UpdateServiceHotel;
import com.hotel.booking.exception.AppException;
import com.hotel.booking.exception.ErrorCode;
import com.hotel.booking.model.ServiceCategory;
import com.hotel.booking.model.ServiceHotel;
import com.hotel.booking.model.User;
import com.hotel.booking.repository.CategoryRepository;
import com.hotel.booking.repository.ServiceHotelRepository;
import com.hotel.booking.service.CloudinaryService;
import com.hotel.booking.service.ServiceHotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class IServiceHotelService implements ServiceHotelService {
    final private ServiceHotelRepository serviceHotelRepository;
    final private CloudinaryService cloudinaryService;
    final private CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<?> addServiceHotel(CreateServiceHotel serviceHotel, Principal principal) {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        ServiceHotel service = ServiceHotel.builder()
                .name(serviceHotel.getName())
                .location(serviceHotel.getLocation())
                .capacity(serviceHotel.getCapacity())
                .openTime(serviceHotel.getStartTime())
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
        serviceHotel.setActive(!serviceHotel.getActive());
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
                for(ServiceHotel serviceHotel: serviceHotelRepository.findServiceHotelsByCategory(category)){
                    ServiceDto serviceDto = ServiceDto.builder()
                            .name(serviceHotel.getName())
                            .description(serviceHotel.getDescription())
                            .image(serviceHotel.getImage())
                            .endTime(serviceHotel.getCloseTime())
                            .startTime(serviceHotel.getOpenTime())
                            .capacity(serviceHotel.getCapacity())
                            .location(serviceHotel.getLocation())
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
}
