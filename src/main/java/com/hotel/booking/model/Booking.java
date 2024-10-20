package com.hotel.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255)
    @Column(name = "checkin")
    private String checkin;

    @Size(max = 255)
    @Column(name = "checkout")
    private String checkout;

    @Column(name = "date_begin")
    private Instant dateBegin;

    @Column(name = "date_end")
    private Instant dateEnd;

    @Column(name = "sum_room")
    private Integer sumRoom;

    @Column(name = "sum_price")
    private Integer sumPrice;

    @Size(max = 255)
    @Column(name = "note")
    private String note;

    @Size(max = 255)
    @Nationalized
    @Column(name = "status")
    private String status;

    @Column(name = "create_at")
    private Instant createAt;

    @Size(max = 255)
    @Nationalized
    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_at")
    private Instant updateAt;

    @Size(max = 255)
    @Nationalized
    @Column(name = "update_by")
    private String updateBy;

    @OneToMany(mappedBy = "booking")
    private Set<BookingRoom> bookingRooms = new LinkedHashSet<>();

}