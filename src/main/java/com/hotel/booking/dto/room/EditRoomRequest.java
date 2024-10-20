package com.hotel.booking.dto.room;

import com.hotel.booking.dto.policy.PolicyDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class EditRoomRequest {
    private String name;
    private String description;
    private int price;
    private int adultNumber;
    private int adultMax;
    private int roomRank;
    private List<Integer> roomList;
    private List<PolicyDto> policyList;
    private List<Integer> serviceList;
    private int roomId;
}
