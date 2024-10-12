package io.github.muhammadredin.tokonyadiaapi.repository;

import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String>, JpaSpecificationExecutor<Customer> {
    boolean existsByUserAccount(UserAccount userAccount);
}
