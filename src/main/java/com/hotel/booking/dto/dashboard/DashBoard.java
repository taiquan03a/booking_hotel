package com.hotel.booking.dto.dashboard;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashBoard {
    private Long countBooked;
    private int countUser;
    private int countCustomer;
    private int countService;
    private int countRoom;
    private List<Chart> chart;
    private CircleChart circleChart;
}
