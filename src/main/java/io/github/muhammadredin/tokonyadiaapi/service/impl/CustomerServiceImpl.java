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
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerImageService customerImageService;
    private final AuthService authService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse createCustomer(CustomerRequest request, MultipartFile image) {
        List<String> errors = checkCustomer();

        if (!errors.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());
        Customer customer = customerRepository.saveAndFlush(toCustomer(request));
        CustomerImage customerImage = customerImageService.saveImage(image, customer);
        customer.setCustomerImage(customerImage);
        return toCustomerResponse(customer);
    }

    @Transactional(readOnly = true)
    @Override
    public Customer getOne(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, CustomerResponseMessage.CUSTOMER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerResponse getCustomerById(String id) {
        Customer customer = getOne(id);
        return toCustomerResponse(customer);
    }

    @Transactional(readOnly = true)
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse updateCustomer(String id, CustomerUpdateRequest customer) {
        Customer updatedCustomer = getOne(id);
        updatedCustomer.setName(customer.getName());
        updatedCustomer.setAddress(customer.getAddress());
        customerRepository.save(updatedCustomer);
        return toCustomerResponse(updatedCustomer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse updateCustomerImage(MultipartFile image) {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();

        CustomerImage customerImage = customerImageService.updateImage(image, customer);

        customer.setCustomerImage(customerImage);
        customerRepository.saveAndFlush(customer);
        return toCustomerResponse(customer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerResponse deleteCustomerImage() {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();

        if (customer.getCustomerImage() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        customerImageService.deleteImage(customer.getCustomerImage());

        customer.setCustomerImage(null);
        customerRepository.saveAndFlush(customer);
        return toCustomerResponse(customer);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCustomer() {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();
        deleteCustomerImage();
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
        FileResponse profileImage;

        if (customer.getCustomerImage() == null) {
            profileImage = null;
        } else {
            profileImage = FileResponse.builder()
                    .id(customer.getCustomerImage().getId())
                    .url("/api/images/customers/" + customer.getCustomerImage().getId())
                    .build();
        }

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .address(customer.getAddress())
                .profileImage(profileImage)
                .build();
    }
}
