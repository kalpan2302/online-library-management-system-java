package com.olms.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "publications")
@Data
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer publicationId;

    @Column(nullable = false, length = 150, unique = true)
    private String name;

    private String address;

    // @Column(nullable = false)
    // private Boolean isExist = true;

    @Column(name = "is_exist", nullable = false)
    private Boolean isExist = true;
}