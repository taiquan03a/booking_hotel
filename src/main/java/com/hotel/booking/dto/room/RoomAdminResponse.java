package com.hotel.booking.dto.room;

import com.hotel.booking.dto.policy.PolicyResponse;
import com.hotel.booking.dto.roomDetail.RoomDetailResponse;
import com.hotel.booking.dto.roomService.RoomServiceResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RoomAdminResponse {
    private int id;
    private String name;
    private String description;
    private int price;
    private int adultNumber;
    private int adultMax;
    private int quantity;
    private Double rate;
    private List<PolicyResponse> policyList;
    private List<RoomDetailResponse> roomDetailList;
    private List<RoomServiceResponse> roomServiceList;
}
