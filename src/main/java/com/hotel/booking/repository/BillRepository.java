package com.hotel.booking.repository;

import com.hotel.booking.model.Bill;
import com.hotel.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    @Query("SELECT b.booking FROM Bill b WHERE b.id = :billId")
    Booking findBookingByBillId(@Param("billId") Integer billId);
    List<Bill> findBillByBooking(Booking booking);

    @Query("select bl from Bill bl where bl.booking = :booking and bl.type = :type and bl.status = 'SUCCESS'")
    Bill findBillByBookingAndType(@Param("booking") Booking booking,@Param("type") String type);
}
