package io.github.muhammadredin.tokonyadiaapi.repository;

import io.github.muhammadredin.tokonyadiaapi.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

}
