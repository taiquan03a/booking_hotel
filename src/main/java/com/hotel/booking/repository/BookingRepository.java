package com.hotel.booking.repository;

import com.hotel.booking.model.Booking;
import com.hotel.booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUser(User user);
}
