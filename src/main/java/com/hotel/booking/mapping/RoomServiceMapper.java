package com.hotel.booking.mapping;

import com.hotel.booking.dto.roomService.RoomServiceResponse;
import com.hotel.booking.model.RoomServiceModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;


@Mapper(componentModel = "spring")
public interface RoomServiceMapper {
    RoomServiceMapper INSTANCE = Mappers.getMapper(RoomServiceMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "icon", target = "icon")
    RoomServiceResponse toRoomServiceResponse(RoomServiceModel roomServiceModel);

    // Phương thức chuyển đổi List<RoomServiceModel> sang List<RoomServiceResponse>
    List<RoomServiceResponse> toRoomServiceResponseList(List<RoomServiceModel> roomServiceModels);
}
