package com.hotel.booking.repository;

import com.hotel.booking.model.Room;
import com.hotel.booking.model.RoomRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findAllByRoomRank(RoomRank rank);
}
