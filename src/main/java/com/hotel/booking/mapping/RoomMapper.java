package com.hotel.booking.mapping;

import com.hotel.booking.dto.room.RoomResponse;
import com.hotel.booking.dto.roomDetail.RoomDetailResponse;
import com.hotel.booking.model.Room;
import com.hotel.booking.model.RoomDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    @Mapping(source = "roomRank.name", target = "roomRank")
    RoomResponse toRoomResponse(Room room);
    List<RoomResponse> toRoomResponseList(List<Room> rooms);
}
