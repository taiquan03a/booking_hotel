package com.hotel.booking.dto.serviceHotel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistoryBookingServiceResponse {
    private int serviceId;
    private String userName;
    private String phone;
    private String serviceName;
    private String price;
    private String statusPayment;
}
