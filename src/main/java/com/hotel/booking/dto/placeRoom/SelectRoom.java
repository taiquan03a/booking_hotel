package com.hotel.booking.dto.placeRoom;

import lombok.Data;
import java.util.*;

@Data
public class SelectRoom {
    private int roomNumberId;
    private int adults;
    private int children;
    private int infants;
    private List<Integer> listServiceId;
}
