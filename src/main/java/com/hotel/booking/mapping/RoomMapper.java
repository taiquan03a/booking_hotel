package com.hotel.booking.mapping;

import com.hotel.booking.dto.room.RoomAdminResponse;
import com.hotel.booking.dto.room.RoomResponse;
import com.hotel.booking.dto.roomDetail.RoomDetailResponse;
import com.hotel.booking.model.Room;
import com.hotel.booking.model.RoomDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PolicyMapper.class, RoomDetailMapper.class, RoomServiceMapper.class})
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    @Mapping(source = "roomRank.id",target = "roomRankId")
    @Mapping(source = "roomRank.name", target = "roomRank")
    @Mapping(source = "policies", target = "policyList")
    @Mapping(source = "roomDetails", target = "roomDetailList")
    @Mapping(source = "service", target = "roomServiceList")
    RoomAdminResponse toRoomResponse(Room room);
    List<RoomAdminResponse> toRoomResponseList(List<Room> rooms);
}
