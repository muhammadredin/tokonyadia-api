package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.CustomerResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerUpdateRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchCustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CustomerResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.FileResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.entity.CustomerImage;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.repository.CustomerRepository;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerImageService;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerService;
import io.github.muhammadredin.tokonyadiaapi.specification.CustomerSpecification;
import io.github.muhammadredin.tokonyadiaapi.util.PagingUtil;
import io.github.muhammadredin.tokonyadiaapi.util.SortUtil;
import io.github.muhammadredin.tokonyadiaapi.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerImageService customerImageService;
    private final ValidationUtil validationUtil;
    private final AuthService authService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse createCustomer(CustomerRequest request, MultipartFile image) {
        log.info("Creating new customer with request: {}", request);
        validationUtil.validate(request);
        List<String> errors = checkCustomer();

        if (!errors.isEmpty()) {
            log.warn("Customer creation failed due to errors: {}", errors);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());
        }

        Customer customer = customerRepository.saveAndFlush(toCustomer(request));
        log.info("Customer created successfully with ID: {}", customer.getId());

        CustomerImage customerImage = customerImageService.saveImage(image, customer);
        customer.setCustomerImage(customerImage);

        log.info("Customer image saved for customer ID: {}", customer.getId());

        return toCustomerResponse(customer);
    }

    @Transactional(readOnly = true)
    @Override
    public Customer getOne(String id) {
        log.info("Fetching customer by ID: {}", id);
        return customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, CustomerResponseMessage.CUSTOMER_NOT_FOUND);
                });
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerResponse getCustomerById(String id) {
        log.info("Fetching customer response for ID: {}", id);
        Customer customer = getOne(id);
        return toCustomerResponse(customer);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CustomerResponse> searchCustomers(SearchCustomerRequest request) {
        log.info("Searching for customers with request: {}", request);
        try {
            Sort sortBy = SortUtil.getSort(request.getSort());
            Specification<Customer> specification = CustomerSpecification.customer(request);
            Page<Customer> customers = customerRepository.findAll(specification, PagingUtil.getPageable(request, sortBy));

            log.info("Found {} customers", customers.getTotalElements());
            return customers.map(this::toCustomerResponse);
        } catch (PropertyReferenceException e) {
            log.error("Invalid property reference in sort request: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse updateCustomer(String id, CustomerUpdateRequest request) {
        log.info("Updating customer with ID: {}", id);
        validationUtil.validate(request);

        Customer updatedCustomer = getOne(id);
        updatedCustomer.setName(request.getName());
        updatedCustomer.setAddress(request.getAddress());
        customerRepository.save(updatedCustomer);

        log.info("Customer updated successfully with ID: {}", id);
        return toCustomerResponse(updatedCustomer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse updateCustomerImage(MultipartFile image) {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();

        log.info("Updating customer image for customer ID: {}", customer.getId());
        CustomerImage customerImage = customerImageService.updateImage(image, customer);
        customer.setCustomerImage(customerImage);

        customerRepository.save(customer);
        log.info("Customer image updated successfully for customer ID: {}", customer.getId());
        return toCustomerResponse(customer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse deleteCustomerImage() {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();

        log.info("Deleting customer image for customer ID: {}", customer.getId());
        if (customer.getCustomerImage() == null) {
            log.warn("No image found for customer ID: {}", customer.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        customerImageService.deleteImage(customer.getCustomerImage());
        customer.setCustomerImage(null);
        customerRepository.saveAndFlush(customer);

        log.info("Customer image deleted successfully for customer ID: {}", customer.getId());
        return toCustomerResponse(customer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCustomer() {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();

        log.info("Deleting customer with ID: {}", customer.getId());
        deleteCustomerImage();
        customerRepository.delete(customer);

        log.info("Customer with ID: {} deleted successfully", customer.getId());
    }

    private List<String> checkCustomer() {
        List<String> errors = new ArrayList<>();
        if (customerRepository.existsByUserAccount(authService.getAuthentication())) {
            errors.add(CustomerResponseMessage.CUSTOMER_ALREADY_EXISTS);
            log.warn("Customer already exists for user account: {}", authService.getAuthentication().getId());
        }
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
        FileResponse profileImage = customer.getCustomerImage() != null
                ? FileResponse.builder()
                .id(customer.getCustomerImage().getId())
                .url("/api/images/customers/" + customer.getCustomerImage().getId())
                .build()
                : null;

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .address(customer.getAddress())
                .profileImage(profileImage)
                .build();
    }
}
