package com.hotel.booking.dto.auth;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String email;
    private String password;
    private String newPassword;
    private String newPasswordConfirm;
}
