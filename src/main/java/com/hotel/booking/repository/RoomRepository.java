package com.hotel.booking.repository;

import com.hotel.booking.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    @Query("SELECT r FROM Room r WHERE :roomDetail MEMBER OF r.roomDetails")
    Room findRoomsByRoomDetail(@Param("roomDetail") RoomDetail roomDetail);
    List<Room> findAllByRoomRank(RoomRank rank);

    @Query("SELECT DISTINCT br " +
            "FROM Booking b " +
            "INNER JOIN b.bookingRooms br " +
            "INNER JOIN br.roomDetail rd " +
            "INNER JOIN rd.room r " +
            "INNER JOIN r.service sr " +
            "WHERE b.user = :user " +
            "AND sr.id = :serviceId " +
            "AND current_timestamp <= br.checkout " +
            "AND br.status = 'BOOKED'")
    List<BookingRoom> findRoomIdsByUserIdAndServiceId(@Param("user") User user,
                                                      @Param("serviceId") int serviceId);

    @Query("select distinct r " +
            "from RoomDetail rd " +
            "inner join rd.room r " +
            "where rd.id = :roomDetailId")
    Room findRoomDetailById(@Param("roomDetailId") int roomDetailId);
}
