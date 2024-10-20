package com.hotel.booking.repository;

import com.hotel.booking.model.RoomRank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRankRepository extends JpaRepository<RoomRank, Integer> {
    Page<RoomRank> findByActiveTrue(Pageable pageable);
}
