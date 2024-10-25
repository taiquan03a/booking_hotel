package com.hotel.booking.repository;

import com.hotel.booking.model.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<ServiceCategory, Integer> {
}
