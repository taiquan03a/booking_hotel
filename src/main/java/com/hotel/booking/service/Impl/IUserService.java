package com.hotel.booking.service.Impl;

import com.hotel.booking.dto.ApiResponse;
import com.hotel.booking.dto.auth.EmailDetails;
import com.hotel.booking.dto.auth.OtpRequest;
import com.hotel.booking.dto.auth.ResetPassword;
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
import com.hotel.booking.service.EmailService;
import com.hotel.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

import static com.hotel.booking.constants.ErrorMessage.EMAIL_IN_USE;

@Service
@RequiredArgsConstructor
public class IUserService implements UserService {
    final private UserRepository userRepository;
    final private RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    final private EmailService emailService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

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

    @Override
    public ResponseEntity<?> checkEmail(String email) {
        var existedUser = userRepository.findByEmail(email);
        if (!existedUser.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                    .statusCode(404)
                    .message(String.valueOf(HttpStatus.NOT_FOUND))
                    .description("Email chưa được đăng ký tài khoản.")
                    .timestamp(new Date(System.currentTimeMillis()))
                    .build());
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(CHARACTERS.length());
            otp.append(CHARACTERS.charAt(index));
        }
        User user = existedUser.get();
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setSubject("Reset mật khẩu!");
        emailDetails.setRecipient(user.getEmail());
        emailDetails.setMsgBody("Chào " + email +
                ",\nChúng tôi rất vui thông báo rằng chúng tôi đã nhân được yêu cầu reset mật khẩu của bạn tại Nhóm 7 Hotel.Dưới đây là thông tin tài khoản của bạn:\n"
                + "\nMật khẩu mới dành cho tài khoản " + email + " :"
                + "\n\n" + otp.toString()
                + "\n\n"
                + "\nVới tài khoản này, bạn có thể truy cập Nhom 7 Hotel và tận hưởng các dịch vụ và tính năng mà chúng tôi cung cấp."
                + "\nNếu bạn có bất kỳ câu hỏi hoặc cần hỗ trợ gì, xin đừng ngần ngại liên hệ với chúng tôi tại shoesshopvn03@gmail.com."
                + "\nChúng tôi rất mong được phục vụ bạn và chúc bạn có trải nghiệm tuyệt vời với Shoes Shop."
                + "\nXin chân thành cảm ơn đã lựa chọn chúng tôi."
                + "\n\nTrân trọng,\n" +
                "Nhom 7 Hotel");
        emailService.sendSimpleMail(emailDetails);
        user.setPassword(passwordEncoder.encode(otp.toString()));
        userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("OTP_SUCCESS")
                                .description("Mật khẩu mới đã được gửi đến " + email + ".Vui lòng kiểm tra email của bạn.")
                                .build()
                );
    }

    @Override
    public ResponseEntity<?> checkOtp(OtpRequest otp) {
        return null;
    }

    @Override
    public ResponseEntity<?> resetPassword(ResetPassword resetPassword) {
        return null;
    }

}
