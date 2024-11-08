package com.hotel.booking.dto.booking;

import com.hotel.booking.dto.policy.PolicyDto;
import com.hotel.booking.dto.policy.PolicyResponse;
import com.hotel.booking.dto.roomService.RoomServiceResponse;
import com.hotel.booking.dto.roomService.ServiceRoomSelect;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BookingRoomDetail {
    private int bookingRoomId;
    private int roomNumber;
    private String roomCode;
    private String roomName;
    private String roomType;
    private String image;
    private String checkIn;
    private String checkOut;
    private int adults;
    private int children;
    private int infant;
    private int adultSurcharge;
    private int childSurcharge;
    private int roomPrice;
    private int totalPrice;
    private List<PolicyResponse> policyList;
    private List<ServiceRoomSelect> serviceList;
}
