package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.UserRole;
import io.github.muhammadredin.tokonyadiaapi.dto.request.UserAccountRequest;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.repository.UserAccountRepository;
import io.github.muhammadredin.tokonyadiaapi.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUserAccount(UserAccountRequest request) {
        UserAccount userAccount = toUserAccount(request);
        List<String> errors = checkUserAccount(userAccount);
        if (!errors.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());
        userAccountRepository.save(userAccount);
    }


    @Override
    public UserAccount getOne(String id) {
        return (UserAccount) loadUserByUsername(id);
    }

    @Override
    public UserDetails loadUserByUsername(String credential) throws UsernameNotFoundException {
        Optional<UserAccount> userAccount = userAccountRepository.findByUsername(credential);
        if (userAccount.isPresent()) return userAccount.get();

        userAccount = userAccountRepository.findByEmail(credential);
        if (userAccount.isPresent()) return userAccount.get();

        userAccount = userAccountRepository.findByPhoneNumber(credential);
        if (userAccount.isPresent()) return userAccount.get();

        userAccount = userAccountRepository.findById(credential);
        if (userAccount.isPresent()) return userAccount.get();


        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    private List<String> checkUserAccount (UserAccount userAccount) {
        List<String> errors = new ArrayList<>();

        if (userAccountRepository.findByUsername(userAccount.getUsername()).isPresent()) errors.add("Username already exists");
        if (userAccountRepository.findByEmail(userAccount.getEmail()).isPresent()) errors.add("Email already exists");
        if (userAccountRepository.findByPhoneNumber(userAccount.getPhoneNumber()).isPresent()) errors.add("Phone number already exists");

        return errors;
    }

    private UserAccount toUserAccount(UserAccountRequest userAccountRequest) {
        return UserAccount.builder()
                .username(userAccountRequest.getUsername())
                .email(userAccountRequest.getEmail())
                .phoneNumber(userAccountRequest.getPhoneNumber())
                .password(passwordEncoder.encode(userAccountRequest.getPassword()))
                .role(UserRole.ROLE_USER)
                .build();
    }
}
