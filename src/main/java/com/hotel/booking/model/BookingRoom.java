package com.hotel.booking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "booking_room")
public class BookingRoom {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_detail_id")
    private RoomDetail roomDetail;

    @Column(name = "sum_adult")
    private Integer sumAdult;

    @Column(name = "sum_children")
    private Integer sumChildren;

    @Column(name = "sum_infant")
    private Integer sumInfant;

    @Column(name = "price")
    private Integer price;

    @Column(name = "service_id")
    private Integer serviceId;

}