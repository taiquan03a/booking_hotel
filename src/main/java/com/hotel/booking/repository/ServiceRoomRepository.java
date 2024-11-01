package com.hotel.booking.repository;

import com.hotel.booking.model.Room;
import com.hotel.booking.model.RoomServiceModel;
import com.hotel.booking.model.ServiceRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRoomRepository extends JpaRepository<ServiceRoom, Integer> {
    ServiceRoom findByRoomAndService(Room room, RoomServiceModel service);
}
