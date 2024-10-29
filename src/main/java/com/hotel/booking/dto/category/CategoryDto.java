package com.hotel.booking.dto.category;

import com.hotel.booking.dto.serviceHotel.ServiceDto;
import com.hotel.booking.model.ServiceHotel;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Nationalized;
import java.util.List;

@Data
@Builder
public class CategoryDto {
    private Integer id;
    private String name;
    private String description;
    List<ServiceDto> serviceHotelList;
}
