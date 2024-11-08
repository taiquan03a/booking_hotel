package com.hotel.booking.dto.booking;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private int roomPrice;
    private int policyPrice;
    private int totalPrice;
    List<BookingRoomResponse> roomCart;
}
