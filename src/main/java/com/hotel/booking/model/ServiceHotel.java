package com.hotel.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "service_hotel")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceHotel {
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
    @Column(name = "location")
    private String location;

    @Size(max = 255)
    @Nationalized
    @Column(name = "capacity")
    private String capacity;

    @Column(name = "open_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss",timezone = "Asia/Ho_Chi_Minh")
    private LocalTime openTime;

    @Column(name = "close_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss",timezone = "Asia/Ho_Chi_Minh")
    private LocalTime closeTime;

    @Size(max = 255)
    @Nationalized
    @Column(name = "description")
    private String description;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "image")
    private String image;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private ServiceCategory category;

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

    @Column(name = "price")
    private Integer price;

    @OneToMany(mappedBy = "serviceHotel")
    @JsonIgnore
    private Set<UserServiceHotel> userServiceHotels = new LinkedHashSet<>();

}