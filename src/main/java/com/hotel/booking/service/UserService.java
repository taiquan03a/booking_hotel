package com.hotel.booking.service;

import com.hotel.booking.dto.auth.OtpRequest;
import com.hotel.booking.dto.auth.ResetPassword;
import com.hotel.booking.dto.user.CreateCustomer;
import com.hotel.booking.dto.user.CreateUserRequest;
import com.hotel.booking.dto.user.EditCustomer;
import com.hotel.booking.dto.user.EditUserRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> getRoles();
    ResponseEntity<?> getUsers();
    ResponseEntity<?> getCustomers();
    ResponseEntity<?> createUser(CreateUserRequest user);
    ResponseEntity<?> createCustomer(CreateCustomer customer);
    ResponseEntity<?> editCustomer(EditCustomer customer);
    ResponseEntity<?> editUser(EditUserRequest user);
    ResponseEntity<?> activeUser(long id);
    ResponseEntity<?> checkEmail(String email);
    ResponseEntity<?> checkOtp(OtpRequest otp);
    ResponseEntity<?> resetPassword(ResetPassword resetPassword);
}
