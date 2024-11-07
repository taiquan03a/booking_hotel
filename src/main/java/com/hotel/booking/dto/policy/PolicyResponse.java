package com.hotel.booking.dto.policy;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PolicyResponse {
    private int id;
    private String type;
    private int typeId;
    private String content;
    private String description;
}
