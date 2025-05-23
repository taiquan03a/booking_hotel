package com.hotel.booking.repository;

import com.hotel.booking.model.Room;
import com.hotel.booking.model.RoomDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomDetailRepository extends JpaRepository<RoomDetail, Integer> {
    boolean existsByRoomNumber(int roomNumber);
    @Query("SELECT rd FROM RoomDetail rd " +
            "LEFT JOIN BookingRoom br ON rd.id = br.roomDetail.id " +
            "LEFT JOIN Booking b ON br.booking.id = b.id " +
            "WHERE (" +
            "   (br.checkin >= :checkout OR br.checkout <= :checkin) " +
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
            "AND CURRENT_TIMESTAMP BETWEEN br.checkin AND br.checkout AND rd.room = :room")
    List<RoomDetail> findCurrentlyBookedRooms(@Param("room") Room room);

    @Query("SELECT count (rd) FROM RoomDetail rd " +
            "JOIN BookingRoom br ON rd.id = br.roomDetail.id " +
            "WHERE br.status = 'CART' " +
            "AND CURRENT_TIMESTAMP BETWEEN br.checkin AND br.checkout")
    Long countRoomCart();
    @Query("SELECT count (rd) FROM RoomDetail rd " +
            "JOIN BookingRoom br ON rd.id = br.roomDetail.id " +
            "WHERE br.status = 'BOOKED' " +
            "AND CURRENT_TIMESTAMP BETWEEN br.checkin AND br.checkout")
    Long countRoomBooked();

    @Query("select count(b) " +
            "from Booking b " +
            "where CAST(b.createAt AS DATE) = CURRENT_DATE " +
            "and b.createBy = :username " +
            "and b.status = 'BOOKED'")
    Long countBookedRoom(@Param("username") String username);
}
