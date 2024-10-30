package com.hotel.booking.dto.user;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class CreateCustomer {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private Date birthday;
}
