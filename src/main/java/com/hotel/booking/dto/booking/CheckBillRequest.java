package com.hotel.booking.dto.booking;

import lombok.Data;

@Data
public class CheckBillRequest {
    private int paymentId;
    private String transId;
}
