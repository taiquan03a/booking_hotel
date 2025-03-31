package com.hotel.booking.repository;

import com.hotel.booking.model.BookingRoom;
import com.hotel.booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface BookingRoomRepository extends JpaRepository<BookingRoom, Integer> {
    @Query("select br " +
            "from Booking b " +
            "inner join b.bookingRooms br " +
            "where b.user = :user " +
            "and br.status ='BOOKED' " +
            "and br.checkout >= current_time")
    List<BookingRoom> findBookingRoomByUser(@Param("user") User user);
}
