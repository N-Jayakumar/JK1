package com.jk1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseAuditEntity {

    @NotBlank
    @Column(name = "street", nullable = false)
    private String street;

    @NotBlank
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank
    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @NotBlank
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @NotBlank
    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;

    @NotBlank
    @Column(name = "address_type", nullable = false, length = 50)
    private String addressType; // Home, Office, Other

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
