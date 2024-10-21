package io.github.muhammadredin.tokonyadiaapi.service.impl;

import io.github.muhammadredin.tokonyadiaapi.constant.UserRole;
import io.github.muhammadredin.tokonyadiaapi.constant.ValidationErrorMessage;
import io.github.muhammadredin.tokonyadiaapi.dto.request.ForgotPasswordRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PasswordResetRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.UserAccountRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.ForgotPasswordResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.repository.UserAccountRepository;
import io.github.muhammadredin.tokonyadiaapi.service.PasswordResetTokenService;
import io.github.muhammadredin.tokonyadiaapi.service.UserAccountService;
import io.github.muhammadredin.tokonyadiaapi.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailServiceImpl emailService;
    private final PasswordEncoder passwordEncoder;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createUserAccount(UserAccountRequest request) {
        // Validate the incoming user account request
        validationUtil.validate(request);

        // Convert DTO to UserAccount entity
        UserAccount userAccount = toUserAccount(request);

        // Check for existing user accounts with the same username, email, or phone number
        List<String> errors = checkUserAccount(userAccount);
        if (!errors.isEmpty()) {
            log.error("User account creation failed: {}", errors);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());
        }

        // Save the new user account to the repository
        userAccountRepository.save(userAccount);
        log.info("User account created successfully for username: {}", userAccount.getUsername());
    }

    @Transactional(readOnly = true)
    @Override
    public UserAccount getOne(String id) {
        // Load user by username or ID
        UserAccount userAccount = (UserAccount) loadUserByUsername(id);
        log.info("Fetched user account details for ID: {}", id);
        return userAccount;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String credential) throws UsernameNotFoundException {
        // Attempt to find user by username, email, phone number, or ID
        Optional<UserAccount> userAccount = userAccountRepository.findByUsername(credential);
        if (userAccount.isPresent()) return userAccount.get();

        userAccount = userAccountRepository.findByEmail(credential);
        if (userAccount.isPresent()) return userAccount.get();

        userAccount = userAccountRepository.findByPhoneNumber(credential);
        if (userAccount.isPresent()) return userAccount.get();

        userAccount = userAccountRepository.findById(credential);
        if (userAccount.isPresent()) return userAccount.get();

        log.error("User not found for credential: {}", credential);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad Credentials");
    }

    @Transactional(readOnly = true)
    @Override
    public UserAccount getOneByEmail(String email) {
        log.info("Fetched user account details for email: {}", email);
        return userAccountRepository.findByEmail(email)
                .orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ForgotPasswordResponse createPasswordResetRequest(ForgotPasswordRequest request) {
        UserAccount userAccount = getOneByEmail(request.getEmail());

        if (userAccount != null) {
            String token = passwordResetTokenService.generatePasswordResetToken(userAccount.getId());
            String url = "localhost:8081/api/user/reset-password?token=" + token;

            // TODO: Gunakan java mail ketika sudah di production
//            emailService.sendPasswordResetEmail(userAccount.getEmail(), url);

            // TODO: Jangan gunakan ForgotPasswordResponse di production, return harus diubah void
            return ForgotPasswordResponse.builder()
                    .passwordResetToken(token)
                    .build();
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void passwordReset(PasswordResetRequest request) {
        String userId = passwordResetTokenService.getUserIdByPasswordResetToken(request.getToken());
        if (!passwordResetTokenService.isPasswordResetTokenValid(userId, request.getToken()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Password Reset Token");

        UserAccount userAccount = getOne(userId);
        userAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        userAccountRepository.save(userAccount);

        passwordResetTokenService.deletePasswordResetToken(userId);
    }

    private List<String> checkUserAccount(UserAccount userAccount) {
        List<String> errors = new ArrayList<>();

        // Validate if username, email, or phone number already exists
        if (userAccountRepository.findByUsername(userAccount.getUsername()).isPresent()) {
            errors.add(ValidationErrorMessage.USERNAME_EXIST);
        }
        if (userAccountRepository.findByEmail(userAccount.getEmail()).isPresent()) {
            errors.add(ValidationErrorMessage.EMAIL_EXIST);
        }
        if (userAccountRepository.findByPhoneNumber(userAccount.getPhoneNumber()).isPresent()) {
            errors.add(ValidationErrorMessage.PHONE_NUMBER_EXIST);
        }

        return errors;
    }

    private UserAccount toUserAccount(UserAccountRequest userAccountRequest) {
        // Convert DTO to UserAccount entity
        return UserAccount.builder()
                .username(userAccountRequest.getUsername())
                .email(userAccountRequest.getEmail())
                .phoneNumber(userAccountRequest.getPhoneNumber())
                .password(passwordEncoder.encode(userAccountRequest.getPassword()))
                .role(UserRole.ROLE_USER)
                .build();
    }
}
