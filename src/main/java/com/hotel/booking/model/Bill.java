package com.hotel.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bill")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "payment_date")
    private String paymentDate;

    @Column(name = "trans_id")
    private String transId;

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
    private LocalDateTime createAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

}