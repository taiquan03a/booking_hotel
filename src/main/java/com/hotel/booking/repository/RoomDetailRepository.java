package com.hotel.booking.repository;

import com.hotel.booking.model.RoomDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomDetailRepository extends JpaRepository<RoomDetail, Integer> {
}
