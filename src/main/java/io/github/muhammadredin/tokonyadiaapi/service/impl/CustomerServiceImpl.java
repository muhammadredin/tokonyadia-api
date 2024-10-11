package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.CustomerResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchCustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CustomerResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.repository.CustomerRepository;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerService;
import io.github.muhammadredin.tokonyadiaapi.specification.CustomerSpecification;
import io.github.muhammadredin.tokonyadiaapi.util.PagingUtil;
import io.github.muhammadredin.tokonyadiaapi.util.SortUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CustomerResponseMessage.CUSTOMER_NOT_FOUND);
        }
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
    public CustomerResponse updateCustomer(String id, CustomerRequest customer) {
        Customer updatedCustomer = customerRepository.findById(id).orElse(null);
        if (updatedCustomer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CustomerResponseMessage.CUSTOMER_NOT_FOUND);
        }
        updatedCustomer.setName(customer.getName());
        updatedCustomer.setAddress(customer.getAddress());
        customerRepository.save(updatedCustomer);
        return toCustomerResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(String id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CustomerResponseMessage.CUSTOMER_NOT_FOUND);
        }
        customerRepository.delete(customer);
    }

    private Customer toCustomer(CustomerRequest customer) {
        return Customer.builder()
                .name(customer.getName())
                .address(customer.getAddress())
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
