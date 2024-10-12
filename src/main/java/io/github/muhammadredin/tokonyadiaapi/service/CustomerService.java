package io.github.muhammadredin.tokonyadiaapi.service;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerUpdateRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchCustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CustomerResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest customer);

    Customer getOne(String id);

    CustomerResponse getCustomerById(String id);
    Page<CustomerResponse> searchCustomers(SearchCustomerRequest request);
    CustomerResponse updateCustomer(String id, CustomerUpdateRequest customer);
    void deleteCustomer(String id);
}
