package com.habbashx.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name", nullable = false , length = 100)
    private String fullName;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false , length = 100)
    private String city;

    @Column(name = "state_name", length = 100)
    private String stateName;

    @Column(name = "postal_code",nullable = false, length = 20)
    private String postalCode;

    @Column(nullable = false , length = 100)
    private String country;
}
