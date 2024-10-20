package com.hotel.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bed")
public class Bed {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 50)
    @Nationalized
    @Column(name = "name", length = 50)
    private String name;

    @Size(max = 255)
    @Nationalized
    @Column(name = "description")
    private String description;

    @Column(name = "create_at")
    private Instant createAt;

    @Size(max = 50)
    @Nationalized
    @Column(name = "create_by", length = 50)
    private String createBy;

    @Column(name = "update_at")
    private Instant updateAt;

    @Size(max = 50)
    @Nationalized
    @Column(name = "update_by", length = 50)
    private String updateBy;

    @OneToMany(mappedBy = "bed")
    private List<RoomBed> roomBeds = new ArrayList<>();

}