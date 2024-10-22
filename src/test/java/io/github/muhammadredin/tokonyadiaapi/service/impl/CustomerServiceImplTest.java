package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.CustomerUpdateRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.SearchCustomerRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.CustomerResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.Customer;
import io.github.muhammadredin.tokonyadiaapi.entity.CustomerImage;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.repository.CustomerRepository;
import io.github.muhammadredin.tokonyadiaapi.service.AuthService;
import io.github.muhammadredin.tokonyadiaapi.service.CustomerImageService;
import io.github.muhammadredin.tokonyadiaapi.service.OrderService;
import io.github.muhammadredin.tokonyadiaapi.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerImageService customerImageService;
    @Mock
    private ValidationUtil validationUtil;
    @Mock
    private AuthService authService;
    @Mock
    private OrderService orderService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnCustomerResponseWhenCreateCustomer() {
        CustomerRequest request = new CustomerRequest();
        request.setName("John Doe");
        request.setAddress("Jl. Benua");

        MultipartFile image = mock(MultipartFile.class);
        when(image.getOriginalFilename()).thenReturn("image.jpg");

        UserAccount userAccount = new UserAccount();
        when(authService.getAuthentication()).thenReturn(userAccount);

        Customer savedCustomer = new Customer();
        savedCustomer.setId("customer-1");
        savedCustomer.setName(request.getName());
        when(customerRepository.saveAndFlush(any(Customer.class))).thenReturn(savedCustomer);

        CustomerImage customerImage = new CustomerImage();
        customerImage.setId("image-1");
        when(customerImageService.saveImage(image, savedCustomer)).thenReturn(customerImage);

        CustomerResponse response = customerService.createCustomer(request, List.of(image));

        assertNotNull(response);
        assertEquals("customer-1", response.getId());
        assertEquals("John Doe", response.getName());
        assertNotNull(response.getProfileImage());
        assertEquals("image-1", response.getProfileImage().getId());

        verify(validationUtil).validate(request);
        verify(customerRepository).saveAndFlush(any(Customer.class));
        verify(customerImageService).saveImage(image, savedCustomer);
    }

    @Test
    void shouldThrowExceptionWhenCreateCustomerWithMoreThanOneImage() {
        CustomerRequest request = new CustomerRequest();
        request.setName("John Doe");

        MultipartFile image1 = mock(MultipartFile.class);
        MultipartFile image2 = mock(MultipartFile.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                customerService.createCustomer(request, List.of(image1, image2))
        );

        assertEquals("400 BAD_REQUEST \"Can't send more than one image\"", exception.getMessage());
    }

    @Test
    void shouldReturnCustomerWhenGetById() {
        Customer customer = new Customer();
        customer.setId("customer-1");
        when(customerRepository.findById("customer-1")).thenReturn(Optional.of(customer));

        Customer result = customerService.getOne("customer-1");

        assertNotNull(result);
        assertEquals("customer-1", result.getId());
        verify(customerRepository).findById("customer-1");
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        when(customerRepository.findById("customer-1")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                customerService.getOne("customer-1")
        );

        assertEquals("404 NOT_FOUND \"Customer not found\"", exception.getMessage());
    }

    @Test
    void shouldReturnCustomerResponseWhenUpdateCustomer() {
        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setName("John Doe Vincent");
        request.setAddress("Jl. Baru");

        Customer existingCustomer = new Customer();
        existingCustomer.setId("customer-1");
        when(customerRepository.findById("customer-1")).thenReturn(Optional.of(existingCustomer));

        CustomerResponse response = customerService.updateCustomer("customer-1", request);

        assertNotNull(response);
        assertEquals("customer-1", response.getId());
        assertEquals("John Doe Vincent", response.getName());
        verify(customerRepository).save(existingCustomer);
        verify(validationUtil).validate(request);
    }
}