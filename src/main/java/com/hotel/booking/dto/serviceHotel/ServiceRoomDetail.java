package com.hotel.booking.dto.serviceHotel;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ServiceRoomDetail {
    private int serviceRoomId;
    private String serviceRoomName;
    private String serviceRoomDescription;
    private String customerName;
    private List<BookedServiceRoom> bookedServiceRoomList;

}
