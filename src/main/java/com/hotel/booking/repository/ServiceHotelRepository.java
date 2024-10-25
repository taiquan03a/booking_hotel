package com.hotel.booking.repository;

import com.hotel.booking.model.ServiceHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceHotelRepository extends JpaRepository<ServiceHotel, Integer> {
}
