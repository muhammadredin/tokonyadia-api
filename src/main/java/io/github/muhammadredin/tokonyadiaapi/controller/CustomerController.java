package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CommonResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CustomerResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomersHandler(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort
    ) {
        PagingAndSortingRequest request = PagingAndSortingRequest.builder()
                .page(page)
                .size(size)
                .sort(sort)
                .build();
        return ResponseUtil.buildResponsePaging(
                HttpStatus.OK,
                "Customer successfully fetched from database",
                customerService.getAllCustomers(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                "Customers successfully fetched from database",
                customerService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(
            @RequestBody CustomerRequest customer
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.CREATED,
                "Customer successfully created",
                customerService.createCustomer(customer)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable String id,
            @RequestBody CustomerRequest customer
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                "Customer successfully updated",
                customerService.updateCustomer(id, customer)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(
            @PathVariable String id
    ) {
        customerService.deleteCustomer(id);
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                "Customer successfully deleted",
                null
        );
    }
}
