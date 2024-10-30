package com.hotel.booking.dto.user;

import com.hotel.booking.model.Role;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class EditUserRequest {
    private long userId;
    private String email;
    //private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private Date birthday;
    private long roleId;
}
