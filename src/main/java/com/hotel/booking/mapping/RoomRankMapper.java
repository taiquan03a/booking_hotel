package com.hotel.booking.mapping;

import com.hotel.booking.dto.AmenityDto;
import com.hotel.booking.dto.ImageDto;
import com.hotel.booking.dto.bed.BedDto;
import com.hotel.booking.dto.rankRoom.RankRoomResponseAdmin;
import com.hotel.booking.model.Amenity;
import com.hotel.booking.model.Image;
import com.hotel.booking.model.RoomBed;
import com.hotel.booking.model.RoomRank;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomRankMapper {
    RoomRankMapper INSTANCE = Mappers.getMapper(RoomRankMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "area", source = "area")
    @Mapping(target = "amenity", source = "amenity", qualifiedByName = "toAmenityDtoList")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "images", source = "images", qualifiedByName = "toImagePathList")
    @Mapping(target = "bed", source = "roomBeds", qualifiedByName = "toBedDtoList")
    RankRoomResponseAdmin toRankRoomResponseAdmin(RoomRank roomRank);

    List<RankRoomResponseAdmin> toRankRoomResponseAdminList(List<RoomRank> roomRanks);

    @Named("toAmenityDto")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "icon", source = "icon")
    AmenityDto toAmenityDto(Amenity amenity);
    @Named("toAmenityDtoList")
    default List<AmenityDto> toAmenityDtoList(List<Amenity> amenities) {
        return amenities.stream().map(this::toAmenityDto).toList();
    }

    @Named("toBedDtoList")
    @Mapping(target = "bedId", source = "bed.id")
    @Mapping(target = "name", source = "bed.name")
    @Mapping(target = "quantity", source = "quantity")
    BedDto toBedDto(RoomBed roomBed);
    default List<BedDto> toBedDtoList(List<RoomBed> roomBeds) {
        return roomBeds.stream().map(this::toBedDto).toList();
    }
    @Named("toImagePathList")
    @Mapping(target = "id",source = "id")
    @Mapping(target = "path",source = "path")
    ImageDto toImageDto(Image image);
    default List<ImageDto> toImagePathList(List<Image> images) {
        return images.stream().map(this::toImageDto).toList();
    }
}
