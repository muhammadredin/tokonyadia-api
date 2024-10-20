package io.github.muhammadredin.tokonyadiaapi.service;
import io.github.muhammadredin.tokonyadiaapi.dto.request.*;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CustomerResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request, MultipartFile image);

    Customer getOne(String id);

    CustomerResponse getCustomerById(String id);
    Page<CustomerResponse> searchCustomers(SearchCustomerRequest request);
    CustomerResponse updateCustomer(String id, CustomerUpdateRequest request);

    CustomerResponse updateCustomerImage(MultipartFile image);

    CustomerResponse deleteCustomerImage();

    void deleteCustomer();
}
