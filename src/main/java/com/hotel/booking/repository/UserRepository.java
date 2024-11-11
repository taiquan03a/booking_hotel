package com.hotel.booking.repository;

import com.hotel.booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("select count (u) > 0 from User u JOIN u.roles r WHERE u.email = :email AND r.role != 'ROLE_USER'")
    boolean existsUserByEmail(@Param("email") String email);

    @Query("select count (u) > 0 from User u JOIN u.roles r WHERE u.email = :email AND r.role = 'ROLE_USER'")
    boolean existsCustomerByEmail(@Param("email") String email);

    @Query("select count(u) from User u join u.roles r where r.role = 'ROLE_USER'")
    int countCustomer();

    @Query("select count(u) from User u join u.roles r where r.role != 'ROLE_USER'")
    int countUser();
}
