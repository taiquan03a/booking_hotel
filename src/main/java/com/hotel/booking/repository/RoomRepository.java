package com.hotel.booking.repository;

import com.hotel.booking.model.Room;
import com.hotel.booking.model.RoomDetail;
import com.hotel.booking.model.RoomRank;
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
}
