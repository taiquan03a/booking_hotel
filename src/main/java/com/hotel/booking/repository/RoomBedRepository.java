package com.hotel.booking.repository;

import com.hotel.booking.model.RoomBed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomBedRepository extends JpaRepository<RoomBed, Integer> {
}
