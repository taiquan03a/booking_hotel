package com.hotel.booking.repository;

import com.hotel.booking.model.Room;
import com.hotel.booking.model.RoomDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RoomDetailRepository extends JpaRepository<RoomDetail, Integer> {
    boolean existsByRoomNumber(int roomNumber);
    @Query("SELECT rd FROM RoomDetail rd " +
            "LEFT JOIN BookingRoom br ON rd.id = br.roomDetail.id " +
            "LEFT JOIN Booking b ON br.booking.id = b.id " +
            "WHERE (" +
            "   (b.dateBegin >= :checkout OR b.dateEnd <= :checkin) " +
            "   AND (br.status NOT IN ('BOOKED', 'CHECKED_IN')) " +
            "   OR (br.status = 'CANCEL' AND br.statusTime <= :checkin) " +
            "   OR br.booking.id IS NULL" +
            ") " +
            "AND rd.room = :room AND rd.status = 'AVAILABLE'")
    List<RoomDetail> findAvailableRooms(
            @Param("checkin") LocalDateTime checkin,
            @Param("checkout") LocalDateTime checkout,
            @Param("room") Room room
    );

    @Query("SELECT rd FROM RoomDetail rd " +
            "JOIN BookingRoom br ON rd.id = br.roomDetail.id " +
            "JOIN Booking b ON br.booking.id = b.id " +
            "WHERE br.status = 'BOOKED' " +
            "AND CURRENT_TIMESTAMP BETWEEN b.dateBegin AND b.dateEnd AND rd.room = :room")
    List<RoomDetail> findCurrentlyBookedRooms(@Param("room") Room room);


}
