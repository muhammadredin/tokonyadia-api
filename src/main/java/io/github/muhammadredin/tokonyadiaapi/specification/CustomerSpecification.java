package io.github.muhammadredin.tokonyadiaapi.specification;

import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchCustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CustomerSpecification {
    public static Specification<Customer> customer(SearchCustomerRequest request) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getQuery() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), request.getQuery().toLowerCase() + "%"));
            }

            if (predicates.isEmpty()) return criteriaBuilder.conjunction();

            return criteriaBuilder.and(predicates.toArray(new Predicate[] {}));
        };
    }
}
