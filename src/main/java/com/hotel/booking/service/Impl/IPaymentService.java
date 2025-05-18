package com.hotel.booking.service.Impl;

import com.hotel.booking.model.Bill;
import com.hotel.booking.model.Booking;
import com.hotel.booking.model.BookingRoom;
import com.hotel.booking.repository.BillRepository;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.BookingRoomRepository;
import com.hotel.booking.service.PaymentService;
import com.hotel.booking.service.ZaloPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
public class IPaymentService implements PaymentService {
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingRoomRepository bookingRoomRepository;
    @Autowired
    private ZaloPayService zaloPayService;

    @Override
    public void checkPaymentAsync(BookingRoom bookingRoom, Bill bill,String newServiceId,int servicePrice,String note) {
        CompletableFuture.runAsync(() -> {
            long startTime = System.currentTimeMillis();
            boolean isPaid = false;

            while (System.currentTimeMillis() - startTime < 2 * 60 * 1000) { // Chạy tối đa 15 phút
                try {
                    Map<String, Object> paymentStatus = zaloPayService.getStatusByApptransid(bill.getTransId());
                    System.out.println(paymentStatus.get("returncode"));
                    System.out.println(paymentStatus.get("returncode").equals("1"));
                    System.out.println((Integer) paymentStatus.get("returncode") == 1);

                    if ((Integer) paymentStatus.get("returncode") == 1) { // Nếu thanh toán thành công
                        bookingRoom.setServiceId(newServiceId);
                        int newPrice = bookingRoom.getPrice() + servicePrice;
                        int oldPrice = bookingRoom.getPrice();
                        bookingRoom.setPrice(newPrice);
                        Booking booking = bookingRoom.getBooking();
                        int newSumPrice = booking.getSumPrice() - oldPrice + newPrice;
                        booking.setSumPrice(newSumPrice);
                        booking.setNote(booking.getNote() + "/" + note);
                        bill.setStatus("SUCCESS");
                        billRepository.save(bill);
                        bookingRepository.save(booking);
                        bookingRoomRepository.save(bookingRoom);
                        isPaid = true;
                        break; // Dừng vòng lặp
                    }

                    Thread.sleep(1000); // Chờ 2 phút rồi kiểm tra lại
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!isPaid) {
                bill.setStatus("FAILED");
                billRepository.save(bill);
            }
        }, Executors.newCachedThreadPool()); // Chạy trong thread pool riêng
    }
}

