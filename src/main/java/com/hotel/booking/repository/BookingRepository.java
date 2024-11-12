package com.hotel.booking.repository;

import com.hotel.booking.model.Booking;
import com.hotel.booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUser(User user);
    @Query("select sum(b.sumPrice) " +
            "from Booking b " +
            "where b.status = 'BOOKED' and b.updateAt between :start and :end")
    Long sumPrice(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
