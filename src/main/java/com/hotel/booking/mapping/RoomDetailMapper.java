package com.hotel.booking.mapping;

import com.hotel.booking.dto.policy.PolicyResponse;
import com.hotel.booking.dto.roomDetail.RoomDetailResponse;
import com.hotel.booking.model.Policy;
import com.hotel.booking.model.RoomDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomDetailMapper {
    //RoomDetailMapper INSTANCE = Mappers.getMapper(RoomDetailMapper.class);
    @Mapping(source = "roomNumber",target = "roomNumber")
    @Mapping(source = "roomCode",target = "roomCode")
    @Mapping(source = "status",target = "status")
    RoomDetailResponse toResponse(RoomDetail roomDetail);
    List<RoomDetailResponse> toResponseList(List<RoomDetail> roomDetails);
}
