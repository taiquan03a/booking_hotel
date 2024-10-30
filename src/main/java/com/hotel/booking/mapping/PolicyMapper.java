package com.hotel.booking.mapping;

import com.hotel.booking.dto.policy.PolicyResponse;
import com.hotel.booking.model.Policy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PolicyMapper {

    PolicyMapper INSTANCE = Mappers.getMapper(PolicyMapper.class);
    @Mapping(source = "type.name", target = "type")
    @Mapping(source = "id",target = "id")
    PolicyResponse toResponse(Policy policy);
    List<PolicyResponse> toResponseList(List<Policy> policies);
}

