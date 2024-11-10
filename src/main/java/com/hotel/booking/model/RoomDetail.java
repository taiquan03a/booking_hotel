package com.hotel.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "room_detail")
public class RoomDetail {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private Room room;

    @Size(max = 100)
    @Nationalized
    @Column(name = "room_code", length = 100)
    private String roomCode;

    @Column(name = "room_number")
    private Integer roomNumber;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "location")
    private String location;

    @Size(max = 10)
    @Nationalized
    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "create_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime createAt;

    @Size(max = 255)
    @Nationalized
    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime updateAt;

    @Size(max = 255)
    @Nationalized
    @Column(name = "update_by")
    private String updateBy;

    @JsonIgnore
    @OneToMany(mappedBy = "roomDetail")
    private Set<BookingRoom> bookingRooms = new LinkedHashSet<>();

}