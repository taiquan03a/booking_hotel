package com.hotel.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "policy_type")
public class PolicyType {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 50)
    @Nationalized
    @Column(name = "name", length = 50)
    private String name;

    @Size(max = 50)
    @Nationalized
    @Column(name = "type", length = 50)
    private String type;

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

    @OneToMany(mappedBy = "type")
    private List<Policy> policies = new ArrayList<>();

}