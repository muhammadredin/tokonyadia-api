package io.github.muhammadredin.tokonyadiaapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "no_siup", nullable = false)
    private String noSiup;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "address", nullable = false)
    private String address;

    @OneToMany(mappedBy = "store",fetch = FetchType.LAZY)
    private List<Product> products;
}
