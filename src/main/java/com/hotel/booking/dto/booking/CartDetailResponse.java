package com.hotel.booking.dto.booking;

import com.hotel.booking.dto.policy.PolicyDto;
import com.hotel.booking.dto.roomService.RoomServiceResponse;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CartDetailResponse {
    private int totalRoomPrice;
    private int totalPolicyPrice;
    private int totalBookingPrice;
    private int totalRoomBooking;
    List<BookingRoomDetail> bookingRoomDetails;
}
