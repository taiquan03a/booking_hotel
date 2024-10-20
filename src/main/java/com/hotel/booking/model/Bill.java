package com.hotel.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "bill")
public class Bill {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "booking_id")
    private Integer bookingId;

    @Size(max = 255)
    @Nationalized
    @Column(name = "payment_date")
    private String paymentDate;

    @Size(max = 255)
    @Nationalized
    @Column(name = "payment_amount")
    private String paymentAmount;

    @Size(max = 255)
    @Column(name = "note")
    private String note;

    @Size(max = 255)
    @Nationalized
    @Column(name = "status")
    private String status;

    @Column(name = "create_at")
    private Instant createAt;

}