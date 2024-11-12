package com.hotel.booking.dto.booking;

import lombok.Data;

@Data
public class CreateFeedback {
    private int paymentId;
    private String feedback;
}
