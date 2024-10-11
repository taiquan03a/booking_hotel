package com.hotel.booking.repository;


import com.hotel.booking.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findRoleByRole(String role);
}