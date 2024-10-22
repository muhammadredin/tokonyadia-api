package io.github.muhammadredin.tokonyadiaapi.service;
import io.github.muhammadredin.tokonyadiaapi.dto.request.*;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CustomerResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.OrderResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request, List<MultipartFile> image);

    Customer getOne(String id);

    CustomerResponse getCustomerById(String id);

    Page<OrderResponse> getAllCustomerOrders(String customerId, SearchOrderRequest request);

    Page<CustomerResponse> searchCustomers(SearchCustomerRequest request);
    CustomerResponse updateCustomer(String id, CustomerUpdateRequest request);

    CustomerResponse updateCustomerImage(List<MultipartFile> image);

    CustomerResponse deleteCustomerImage();

    void deleteCustomer();
}
