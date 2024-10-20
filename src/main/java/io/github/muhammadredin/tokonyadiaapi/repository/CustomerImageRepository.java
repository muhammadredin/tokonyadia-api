package io.github.muhammadredin.tokonyadiaapi.repository;

import io.github.muhammadredin.tokonyadiaapi.entity.CustomerImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerImageRepository extends JpaRepository<CustomerImage, String> {
}
