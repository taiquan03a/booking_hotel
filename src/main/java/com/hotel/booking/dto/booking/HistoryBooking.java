package com.hotel.booking.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class HistoryBooking {
    private int bookingId;
    private String paymentStatus;
    private int totalRoomPrice;
    private int totalPolicyPrice;
    private int totalBookingPrice;
    private int totalRoomBooking;
    private String customer;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime bookingDate;
    List<BookingRoomDetail> bookingRoomDetails;
}
