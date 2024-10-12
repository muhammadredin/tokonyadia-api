package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.CustomerResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerUpdateRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchCustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CustomerResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.repository.CustomerRepository;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerService;
import io.github.muhammadredin.tokonyadiaapi.specification.CustomerSpecification;
import io.github.muhammadredin.tokonyadiaapi.util.PagingUtil;
import io.github.muhammadredin.tokonyadiaapi.util.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final AuthService authService;

    @Override
    public CustomerResponse createCustomer(CustomerRequest customer) {
        return toCustomerResponse(customerRepository.save(toCustomer(customer)));
    }

    @Override
    public Customer getOne(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, CustomerResponseMessage.CUSTOMER_NOT_FOUND));
    }

    @Override
    public CustomerResponse getCustomerById(String id) {
        Customer customer = getOne(id);
        return toCustomerResponse(customer);
    }

    @Override
    public Page<CustomerResponse> searchCustomers(SearchCustomerRequest request) {
        try {
            Sort sortBy = SortUtil.getSort(request.getSort());

            Specification<Customer> specification = CustomerSpecification.customer(request);
            Page<Customer> customers = customerRepository.findAll(specification, PagingUtil.getPageable(request, sortBy));

            return customers.map(this::toCustomerResponse);
        } catch (PropertyReferenceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public CustomerResponse updateCustomer(String id, CustomerUpdateRequest customer) {
        Customer updatedCustomer = getOne(id);
        updatedCustomer.setName(customer.getName());
        updatedCustomer.setAddress(customer.getAddress());
        customerRepository.save(updatedCustomer);
        return toCustomerResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(String id) {
        Customer customer = getOne(id);
        customerRepository.delete(customer);
    }

    private Customer toCustomer(CustomerRequest request) {
        UserAccount account = authService.getAuthentication();
        if (customerRepository.existsByUserAccount(account)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer with this user already exists");
        }
        return Customer.builder()
                .name(request.getName())
                .address(request.getAddress())
                .userAccount(account)
                .build();
    }

    private CustomerResponse toCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .address(customer.getAddress())
                .build();
    }
}
