package com.hotel.booking.dto.room;

import com.hotel.booking.dto.policy.PolicyDto;
import com.hotel.booking.dto.roomService.ServiceRoomRequest;
import com.hotel.booking.model.RoomDetail;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateRoomRequest {
    private String name;
    private String description;
    private int price;
    private int adultNumber;
    private int adultMax;
    private int roomRank;
    private List<Integer> roomList;
    private List<PolicyDto> policyList;
    private List<ServiceRoomRequest> serviceList;
}
