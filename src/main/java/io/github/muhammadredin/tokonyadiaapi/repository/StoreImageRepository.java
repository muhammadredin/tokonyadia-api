package io.github.muhammadredin.tokonyadiaapi.repository;

import io.github.muhammadredin.tokonyadiaapi.entity.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreImageRepository extends JpaRepository<StoreImage, String> {
}
