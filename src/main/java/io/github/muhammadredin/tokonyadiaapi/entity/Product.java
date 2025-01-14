package io.github.muhammadredin.tokonyadiaapi.entity;

import io.github.muhammadredin.tokonyadiaapi.constant.TableName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@SuperBuilder
@Table(name = TableName.PRODUCT_TABLE)
public class Product extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<OrderDetails> orderDetails;

    @OneToMany(mappedBy = "product")
    private Set<Cart> cart;

    @OneToMany(mappedBy = "product")
    private List<ProductImage> productImages;
}
