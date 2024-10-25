package com.hotel.booking.dto.room;

import com.hotel.booking.dto.policy.PolicyDto;
import com.hotel.booking.dto.policy.PolicyResponse;
import com.hotel.booking.dto.roomService.RoomServiceResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RoomUserResponse {
    private int id;
    private String name;
    private String description;
    private int price;
    private int adultNumber;
    private int adultMax;
    private String roomRank;
    private int quantity;
    private List<PolicyResponse> policyList;
    private List<RoomServiceResponse> roomServiceList;
}
