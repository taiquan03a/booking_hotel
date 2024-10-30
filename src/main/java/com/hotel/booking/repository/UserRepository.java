package com.hotel.booking.repository;

import com.hotel.booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.role != 'ROLE_USER'")
    List<User> findAllExcludingUserRole();

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.role = 'ROLE_USER'")
    List<User> findAllCustomer();
}
