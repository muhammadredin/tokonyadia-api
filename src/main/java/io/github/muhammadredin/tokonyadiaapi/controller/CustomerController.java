package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.constant.CustomerResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                CustomerResponseMessage.CUSTOMER_GET_SUCCESS,
                customerService.getAllCustomers(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_GET_SUCCESS,
                customerService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(
            @RequestBody CustomerRequest customer
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.CREATED,
                CustomerResponseMessage.CUSTOMER_CREATE_SUCCESS,
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
                CustomerResponseMessage.CUSTOMER_UPDATE_SUCCESS,
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
                CustomerResponseMessage.CUSTOMER_DELETE_SUCCESS,
                null
        );
    }
}
