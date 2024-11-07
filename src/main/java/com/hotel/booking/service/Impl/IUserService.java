package com.hotel.booking.service.Impl;

import com.hotel.booking.dto.ApiResponse;
import com.hotel.booking.dto.user.CreateCustomer;
import com.hotel.booking.dto.user.CreateUserRequest;
import com.hotel.booking.dto.user.EditCustomer;
import com.hotel.booking.dto.user.EditUserRequest;
import com.hotel.booking.exception.AppException;
import com.hotel.booking.exception.ErrorCode;
import com.hotel.booking.exception.ErrorResponse;
import com.hotel.booking.mapping.UserMapper;
import com.hotel.booking.model.EnumRole;
import com.hotel.booking.model.Role;
import com.hotel.booking.model.User;
import com.hotel.booking.repository.RoleRepository;
import com.hotel.booking.repository.UserRepository;
import com.hotel.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.hotel.booking.constants.ErrorMessage.EMAIL_IN_USE;

@Service
@RequiredArgsConstructor
public class IUserService implements UserService {
    final private UserRepository userRepository;
    final private RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> getRoles() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully List role")
                                .data(roleRepository.findAll().stream().filter(role -> !role.getRole().equals(String.valueOf(EnumRole.ROLE_USER))))
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> getUsers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully List user")
                                .data(UserMapper.INSTANCE.usersToUserResponses(userRepository.findAllExcludingUserRole()))
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> getCustomers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully List customer")
                                .data(UserMapper.INSTANCE.usersToUserResponses(userRepository.findAllCustomer()))
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> createUser(CreateUserRequest createUserRequest) {
        if(createUserRequest.getRoleId() == 3){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message(String.valueOf(HttpStatus.BAD_REQUEST))
                    .description("Role là admin hoặc nhân viên.")
                    .timestamp(new Date(System.currentTimeMillis()))
                    .build());
        }
        var existedUser = userRepository.findByEmail(createUserRequest.getEmail());
        if (existedUser.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.builder()
                    .statusCode(409)
                    .message(String.valueOf(HttpStatus.CONFLICT))
                    .description(EMAIL_IN_USE)
                    .timestamp(new Date(System.currentTimeMillis()))
                    .build());
        Set<Role> roles = new HashSet<>();
        Optional<Role> roleOptional = roleRepository.findById(createUserRequest.getRoleId());

        roleOptional.ifPresentOrElse(
                roles::add,
                () -> {
                    throw new AppException(ErrorCode.NOT_FOUND);
                }
        );
        User user = User.builder()
                .email(createUserRequest.getEmail())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .phone(createUserRequest.getPhone())
                .dob(createUserRequest.getBirthday())
                .roles(roles)
                .isActive(true)
                .isEmailActive(true)
                .createdAt(new Date())
                .build();
        userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .message("Successfully create user")
                                .data(UserMapper.INSTANCE.userToUserResponse(user))
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> createCustomer(CreateCustomer customer) {
        var existedUser = userRepository.findByEmail(customer.getEmail());
        if (existedUser.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.builder()
                    .statusCode(409)
                    .message(String.valueOf(HttpStatus.CONFLICT))
                    .description(EMAIL_IN_USE)
                    .timestamp(new Date(System.currentTimeMillis()))
                    .build());
        Set<Role> roles = new HashSet<>();
        Role roleOptional = roleRepository.findRoleByRole(String.valueOf(EnumRole.ROLE_USER));
        roles.add(roleOptional);
        User user = User.builder()
                .email(customer.getEmail())
                .password(passwordEncoder.encode(customer.getPassword()))
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phone(customer.getPhone())
                .dob(customer.getBirthday())
                .roles(roles)
                .isActive(true)
                .isEmailActive(true)
                .createdAt(new Date())
                .build();
        userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .message("Successfully create user")
                                .data(UserMapper.INSTANCE.userToUserResponse(user))
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> editCustomer(EditCustomer customer) {
        User user = userRepository.findById(customer.getUserId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if(!customer.getEmail().equals(user.getEmail())){
            var existedUser = userRepository.findByEmail(customer.getEmail());
            if (existedUser.isPresent())
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.builder()
                        .statusCode(409)
                        .message(String.valueOf(HttpStatus.CONFLICT))
                        .description(EMAIL_IN_USE)
                        .timestamp(new Date(System.currentTimeMillis()))
                        .build());
        }
        user.setEmail(customer.getEmail());
        user.setFirstName(customer.getFirstName());
        user.setLastName(customer.getLastName());
        user.setPhone(customer.getPhone());
        user.setDob(customer.getBirthday());
        userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully edit customer")
                                .data(UserMapper.INSTANCE.userToUserResponse(user))
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> editUser(EditUserRequest user) {
        User user1 = userRepository.findById(user.getUserId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if(!user.getEmail().equals(user.getEmail())){
            var existedUser = userRepository.findByEmail(user.getEmail());
            if (existedUser.isPresent())
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.builder()
                        .statusCode(409)
                        .message(String.valueOf(HttpStatus.CONFLICT))
                        .description(EMAIL_IN_USE)
                        .timestamp(new Date(System.currentTimeMillis()))
                        .build());
        }
        user1.setEmail(user.getEmail());
        user1.setFirstName(user.getFirstName());
        user1.setLastName(user.getLastName());
        user1.setPhone(user.getPhone());
        user1.setDob(user.getBirthday());
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findById(user.getRoleId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND)));
        user1.setRoles(roles);
        userRepository.save(user1);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully edit user")
                                .data(UserMapper.INSTANCE.userToUserResponse(user1))
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> activeUser(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        user.setActive(!user.isActive());
        userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Successfully active/deactive user")
                                .data(UserMapper.INSTANCE.userToUserResponse(user))
                                .build()
                );
    }

}
