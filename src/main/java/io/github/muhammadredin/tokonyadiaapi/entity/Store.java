package io.github.muhammadredin.tokonyadiaapi.entity;

import io.github.muhammadredin.tokonyadiaapi.constant.TableName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = TableName.STORE_TABLE)
public class Store extends Auditable {
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

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private UserAccount userAccount;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<Product> products;

    @OneToOne(mappedBy = "store")
    private StoreImage storeImage;
}
