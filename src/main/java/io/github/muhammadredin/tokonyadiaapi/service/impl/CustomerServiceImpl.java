package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.CustomerResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CartRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerUpdateRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchCustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CartResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CustomerResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Cart;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.entity.Product;
import io.github.muhammadredin.tokonyadiaapi.repository.CartRepository;
import io.github.muhammadredin.tokonyadiaapi.repository.CustomerRepository;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerService;
import io.github.muhammadredin.tokonyadiaapi.service.ProductService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final AuthService authService;

    @Override
    public CustomerResponse createCustomer(CustomerRequest customer) {
        List<String> errors = checkCustomer();

        if (!errors.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());

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

    private List<String> checkCustomer() {
        List<String> errors = new ArrayList<>();
        if (customerRepository.existsByUserAccount(authService.getAuthentication())) errors.add(CustomerResponseMessage.CUSTOMER_ALREADY_EXISTS);
        return errors;
    }

    private Customer toCustomer(CustomerRequest request) {
        return Customer.builder()
                .name(request.getName())
                .address(request.getAddress())
                .userAccount(authService.getAuthentication())
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
