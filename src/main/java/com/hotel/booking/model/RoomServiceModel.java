package com.hotel.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "room_service")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomServiceModel {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "name")
    private String name;

    @Size(max = 255)
    @Nationalized
    @Column(name = "description")
    private String description;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "icon")
    private String icon;

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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "service_room",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id"))
    private List<Room> rooms ;

    @JsonIgnore
    @OneToMany(mappedBy = "service",fetch = FetchType.LAZY)
    private List<ServiceRoom> serviceRooms = new ArrayList<>();

}