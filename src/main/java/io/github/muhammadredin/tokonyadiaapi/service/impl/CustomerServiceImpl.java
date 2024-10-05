package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CustomerResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.repository.CustomerRepository;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerService;
import io.github.muhammadredin.tokonyadiaapi.util.PagingUtil;
import io.github.muhammadredin.tokonyadiaapi.util.SortUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest customer) {
        return toCustomerResponse(customerRepository.save(toCustomer(customer)));
    }

    @Override
    public CustomerResponse getCustomerById(String id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        return toCustomerResponse(customer);
    }

    @Override
    public Page<CustomerResponse> getAllCustomers(PagingAndSortingRequest request) {
        try {
            Sort sortBy = SortUtil.getSort(request.getSort());

            Page<Customer> customers = customerRepository.findAll(PagingUtil.getPageable(request, sortBy));

            return customers.map(this::toCustomerResponse);
        } catch (PropertyReferenceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public CustomerResponse updateCustomer(String id, CustomerRequest customer) {
        Customer updatedCustomer = customerRepository.findById(id).orElse(null);
        if (updatedCustomer == null) {
            throw new RuntimeException("Customer not found");
        }
        updatedCustomer.setName(customer.getName());
        updatedCustomer.setAddress(customer.getAddress());
        updatedCustomer.setEmail(customer.getEmail());
        updatedCustomer.setPhoneNumber(customer.getPhoneNumber());
        customerRepository.save(updatedCustomer);
        return toCustomerResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(String id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        customerRepository.delete(customer);
    }

    private Customer toCustomer(CustomerRequest customer) {
        return Customer.builder()
                .name(customer.getName())
                .address(customer.getAddress())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }

    private CustomerResponse toCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .address(customer.getAddress())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }
}
