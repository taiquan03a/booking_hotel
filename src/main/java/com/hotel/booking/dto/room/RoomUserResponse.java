package com.hotel.booking.dto.room;

import com.hotel.booking.dto.policy.PolicyDto;
import java.util.List;

public class RoomUserResponse {
    private int id;
    private String name;
    private String description;
    private int price;
    private int adultNumber;
    private String roomRank;
    private int quantity;
    private float rate;
    private List<PolicyDto> policyList;
}
