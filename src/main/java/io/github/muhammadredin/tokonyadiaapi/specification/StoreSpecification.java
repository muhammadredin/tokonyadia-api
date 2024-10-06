package io.github.muhammadredin.tokonyadiaapi.specification;

import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchStoreRequest;
import io.github.muhammadredin.tokonyadiaapi.entity.Store;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class StoreSpecification {
    public static Specification<Store> store(SearchStoreRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getQuery() != null) {
                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), request.getQuery().toLowerCase() + "%"),
                        criteriaBuilder.equal(root.get("noSiup"), request.getQuery()),
                        criteriaBuilder.equal(root.get("phoneNumber"), request.getQuery())
                );
                predicates.add(searchPredicate);
            }

            if (predicates.isEmpty()) return criteriaBuilder.conjunction();

            return criteriaBuilder.and(predicates.toArray(new Predicate[] {}));
        };
    }
}
