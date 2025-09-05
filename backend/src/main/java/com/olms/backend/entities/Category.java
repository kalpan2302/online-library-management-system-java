package com.olms.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    private Double fineRatePerDay;

    // @Column(nullable = false)
    // private Boolean isExist = true;

    @Column(name = "is_exist", nullable = false)
    private Boolean isExist = true;
}