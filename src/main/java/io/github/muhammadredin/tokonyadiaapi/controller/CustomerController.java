package io.github.muhammadredin.tokonyadiaapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.muhammadredin.tokonyadiaapi.constant.APIPath;
import io.github.muhammadredin.tokonyadiaapi.constant.CustomerResponseMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.*;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CommonResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CustomerResponse;
import io.github.muhammadredin.tokonyadiaapi.service.CartService;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerService;
import io.github.muhammadredin.tokonyadiaapi.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(APIPath.CUSTOMER_API)
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Customer Management", description = "Operations related to managing customer data, including creation, update, retrieval, and deletion")
public class CustomerController {
    private static class CommonResponseListCustomerResponse extends CommonResponse<Page<CustomerResponse>> {}
    private static class CommonResponseCustomerResponse extends CommonResponse<CustomerResponse> {}

    private final CustomerService customerService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Retrieve all customers",
            description = "Retrieve a paginated list of all customers. Optional query parameters include pagination and sorting.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customers retrieved successfully", content = @Content(schema = @Schema(implementation = CommonResponseListCustomerResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            })
    @PreAuthorize("hasRole('ADMIN')")
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

    @Operation(summary = "Get customer by ID",
            description = "Retrieve the details of a specific customer by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer details retrieved successfully", content = @Content(schema = @Schema(implementation = CommonResponseCustomerResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerByIdHandler(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_GET_SUCCESS,
                customerService.getCustomerById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @GetMapping("/{id}/orders")
    public ResponseEntity<?> getCustomerOrdersHandler(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String orderStatus,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        SearchOrderRequest request = SearchOrderRequest.builder()
                .page(page)
                .size(size)
                .sort(sort)
                .orderStatus(orderStatus)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        return ResponseUtil.buildResponsePaging(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_GET_SUCCESS,
                customerService.getAllCustomerOrders(id, request));
    }

    @Operation(summary = "Create a new customer",
            description = "This endpoint allows the creation of a new customer. The customer data is passed in the request body.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Customer created successfully", content = @Content(schema = @Schema(implementation = CommonResponseCustomerResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping()
    public ResponseEntity<?> createCustomerHandler(
            @RequestParam String customer,
            @RequestParam(name = "image") List<MultipartFile> image
    ) {
        CustomerRequest customerRequest = customerRequestMap(customer);
        return ResponseUtil.buildResponse(
                HttpStatus.CREATED,
                CustomerResponseMessage.CUSTOMER_CREATE_SUCCESS,
                customerService.createCustomer(customerRequest, image)
        );
    }

    @Operation(summary = "Update customer details",
            description = "Update the details of a specific customer by their ID. Authorization is required for admin users or customers with appropriate access.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer updated successfully", content = @Content(schema = @Schema(implementation = CommonResponseCustomerResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomerHandler(
            @PathVariable String id,
            @Valid @RequestBody CustomerUpdateRequest customer
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_UPDATE_SUCCESS,
                customerService.updateCustomer(id, customer)
        );
    }

    @Operation(summary = "Update customer photo",
            description = "Update the photo of a specific customer by their ID. Authorization is required for admin users or customers with appropriate access.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer updated successfully", content = @Content(schema = @Schema(implementation = CommonResponseCustomerResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @PutMapping(path ="/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCustomerImageHandler(
            @PathVariable String id,
            @RequestParam List<MultipartFile> image
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_UPDATE_SUCCESS,
                customerService.updateCustomerImage(image)
        );
    }

    @Operation(summary = "Delete customer photo",
            description = "Delete the photo of a specific customer by their ID. Authorization is required for admin users or customers with appropriate access.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer updated successfully", content = @Content(schema = @Schema(implementation = CommonResponseCustomerResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<?> deleteCustomerImageHandler(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_UPDATE_SUCCESS,
                customerService.deleteCustomerImage()
        );
    }

    @Operation(summary = "Delete customer by ID",
            description = "Delete a specific customer by their ID. Only authorized users can perform this action.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer deleted successfully", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @permissionEvaluationServiceImpl.customerServiceEval(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomerHandler(
            @PathVariable String id
    ) {
        customerService.deleteCustomer();
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                CustomerResponseMessage.CUSTOMER_DELETE_SUCCESS,
                null
        );
    }



    private CustomerRequest customerRequestMap(String request) {
        try {
            return objectMapper.readValue(request, CustomerRequest.class);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
