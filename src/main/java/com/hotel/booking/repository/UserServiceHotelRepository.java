package com.hotel.booking.repository;

import com.hotel.booking.model.UserServiceHotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserServiceHotelRepository extends JpaRepository<UserServiceHotel, Long> {
}
