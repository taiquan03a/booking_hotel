package com.hotel.booking.controller;

import com.hotel.booking.dto.user.CreateCustomer;
import com.hotel.booking.dto.user.CreateUserRequest;
import com.hotel.booking.dto.user.EditCustomer;
import com.hotel.booking.dto.user.EditUserRequest;
import com.hotel.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    final private UserService userService;

    @GetMapping("role")
    public ResponseEntity<?> getRole() {
        return userService.getRoles();
    }
    @GetMapping("user")
    public ResponseEntity<?> getUser() {
        return userService.getUsers();
    }
    @GetMapping("customer")
    public ResponseEntity<?> getCustomer() {
        return userService.getCustomers();
    }
    @PostMapping("user/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
       return  userService.createUser(request);
    }
    @PostMapping("customer/create")
    public ResponseEntity<?> createCustomer(@RequestBody CreateCustomer request) {
        return userService.createCustomer(request);
    }
    @PutMapping("user/edit")
    public ResponseEntity<?> editUser(@RequestBody EditUserRequest request) {
        return userService.editUser(request);
    }
    @PutMapping("customer/edit")
    public ResponseEntity<?> editCustomer(@RequestBody EditCustomer request) {
        return userService.editCustomer(request);
    }
    @GetMapping("active/{id}")
    public ResponseEntity<?> getActiveUser(@PathVariable long id) {
        return userService.activeUser(id);
    }
}
