package io.github.muhammadredin.tokonyadiaapi.specification;

import io.github.muhammadredin.tokonyadiaapi.constant.OrderStatus;
import io.github.muhammadredin.tokonyadiaapi.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class OrderSpecification {
    public static Specification<Order> storeTransactionDetails(Store store, String startDate, String endDate, OrderStatus status) {
        return (root, query, criteriaBuilder) -> {
            // Join product
            Join<Order, Invoice> invoiceJoin = root.join("invoice");

            Join<Invoice, Customer> customerJoin = invoiceJoin.join("customer");

            // Join transaction
            Join<Order, OrderDetails> transactionDetailJoin = root.join("orderDetails");

            // Join customer
            Join<OrderDetails, Product> productJoin = transactionDetailJoin.join("product");

            // Predicate for store_id
            Predicate storeIdPredicate = criteriaBuilder.and(
                    criteriaBuilder.equal(productJoin.get("store"), store)
            );

            // Predicate for order_date
            LocalDateTime startDateTime = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(); // Start of the day
            LocalDateTime endDateTime = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).plusDays(1).atStartOfDay();

            Predicate datePredicate = criteriaBuilder.between(root.get("orderDate"), startDateTime, endDateTime);

            // Predicate for order_status
            Predicate statusPredicate = criteriaBuilder.conjunction();
            if (status != null) {
                statusPredicate = criteriaBuilder.equal(root.get("orderStatus"), status);
            }

            // Combine predicates
            Predicate combinedPredicate = criteriaBuilder.and(storeIdPredicate, datePredicate, statusPredicate);

            // Specify group by clause (you can only groupBy for queries returning aggregate results)
            query.groupBy(
                    root.get("id"),
                    customerJoin.get("name"),
                    root.get("orderStatus")
            );

            // Return the combined predicate
            return combinedPredicate;
        };
    }

    public static Specification<Order> customerOrderDetails(Customer customer, String startDate, String endDate, OrderStatus status) {
        return (root, query, criteriaBuilder) -> {
            // Join product
            Join<Order, Invoice> invoiceJoin = root.join("invoice");

            Predicate customerPredicate = criteriaBuilder.equal(invoiceJoin.get("customer"), customer);

            // Predicate for order_date
            LocalDateTime startDateTime = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(); // Start of the day
            LocalDateTime endDateTime = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).plusDays(1).atStartOfDay();

            Predicate datePredicate = criteriaBuilder.between(root.get("orderDate"), startDateTime, endDateTime);

            // Predicate for order_status
            Predicate statusPredicate = criteriaBuilder.conjunction();
            if (status != null) {
                statusPredicate = criteriaBuilder.equal(root.get("orderStatus"), status);
            }

            // Combine predicates
            return criteriaBuilder.and(customerPredicate, datePredicate, statusPredicate);
        };
    }
}
