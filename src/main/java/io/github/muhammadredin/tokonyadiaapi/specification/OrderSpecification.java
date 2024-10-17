package io.github.muhammadredin.tokonyadiaapi.specification;

import io.github.muhammadredin.tokonyadiaapi.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class OrderSpecification {
    public static Specification<Order> storeTransactionDetails(Store store) {
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
                    // TODO: nyalakan ketika sudah menggunakan midtrans
//                    criteriaBuilder.notEqual(invoiceJoin.get("paymentStatus"), PaymentStatus.PENDING)
            );


            // Specify group by clause (you can only groupBy for queries returning aggregate results)
            query.groupBy(
                    root.get("id"),
                    customerJoin.get("name"),
                    root.get("orderStatus")
            );

            // Return the combined predicate
            return storeIdPredicate;
        };
    }
}
