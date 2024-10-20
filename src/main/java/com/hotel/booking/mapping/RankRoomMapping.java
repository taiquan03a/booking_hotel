//package com.hotel.booking.mapping;
//
//import com.hotel.booking.dto.rankRoom.RankRoomResponseUser;
//import com.hotel.booking.model.Amenity;
//import com.hotel.booking.model.RoomRank;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.factory.Mappers;
//
//import java.util.List;
//
//@Mapper(componentModel = "spring")
//public interface RankRoomMapping {
//    RankRoomMapping INSTANCE = Mappers.getMapper(RankRoomMapping.class);
//
//    @Mapping(source = "id",target = "id")
//    @Mapping(source = "name",target = "name")
//    @Mapping(source = "bed",target = "bed")
//    @Mapping(source = "area",target = "area")
//    @Mapping(source = "image",target = "image")
//    RankRoomResponseUser toRankDto(RoomRank roomRank);
//}