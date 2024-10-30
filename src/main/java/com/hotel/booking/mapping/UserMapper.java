package com.hotel.booking.mapping;

import com.hotel.booking.dto.user.UserResponse;
import com.hotel.booking.model.Role;
import com.hotel.booking.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "birthday", source = "dob")
    @Mapping(target = "createAt",source = "createdAt")
    @Mapping(target = "role",source = "roles",qualifiedByName = "toListRole")
    UserResponse userToUserResponse(User user);

    @Mapping(target = "dob", source = "birthday")
    User userResponseToUser(UserResponse userResponse);

    List<UserResponse> usersToUserResponses(List<User> users);

    List<User> userResponsesToUsers(List<UserResponse> userResponses);

    @Named("toListRole")
    default Role getFirstRole(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        return roles.stream()
                .findFirst()
                .orElse(null);
    }
}

