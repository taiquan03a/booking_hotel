package com.hotel.booking.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hotel.booking.model.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserResponse {
    private int id;
    private String email;
    //private String password;
    private String firstName;
    private String lastName;
    private String phone;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    private LocalDate birthday;
    private Role role;
    private boolean emailActive;
    private boolean active;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime createAt;
}
