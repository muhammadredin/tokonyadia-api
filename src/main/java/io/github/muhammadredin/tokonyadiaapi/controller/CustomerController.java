package io.github.muhammadredin.tokonyadiaapi.controller;

import io.github.muhammadredin.tokonyadiaapi.constant.CustomerResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerUpdateRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PagingAndSortingRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchCustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('STORE')")
    @GetMapping("/search")
    public ResponseEntity<?> searchCustomersHandler(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort
    ) {
        SearchCustomerRequest request = SearchCustomerRequest.builder()
                .query(q)
                .page(page)
                .size(size)
                .build();
        return ResponseUtil.buildResponsePaging(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_GET_SUCCESS,
                customerService.searchCustomers(request)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_GET_SUCCESS,
                customerService.getCustomerById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
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

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable String id,
            @RequestBody CustomerUpdateRequest customer
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_UPDATE_SUCCESS,
                customerService.updateCustomer(id, customer)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
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
