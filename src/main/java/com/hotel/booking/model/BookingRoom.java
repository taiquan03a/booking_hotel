package com.hotel.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "booking_room")
public class BookingRoom {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_detail_id")
    private RoomDetail roomDetail;

    @Column(name = "status_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime statusTime;
    @Column(name = "status")
    private String status;

    @Column(name = "adult_surcharge")
    private Integer adultSurcharge;

    @Column(name = "child_surcharge")
    private Integer childSurcharge;

    @Column(name = "sum_adult")
    private Integer sumAdult;

    @Column(name = "sum_children")
    private Integer sumChildren;

    @Column(name = "sum_infant")
    private Integer sumInfant;

    @Column(name = "price")
    private Integer price;

    @Column(name = "service_id")
    private String serviceId;

    @Column(name = "checkin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime checkin;

    @Column(name = "checkout")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime checkout;

}