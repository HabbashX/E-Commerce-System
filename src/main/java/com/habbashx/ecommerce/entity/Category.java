package com.habbashx.ecommerce.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false , unique = true , length = 100)
    private String categoryName;

    @ManyToMany(mappedBy = "categories")
    private final Set<Product> products = new HashSet<>();
}
