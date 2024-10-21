package io.github.muhammadredin.tokonyadiaapi.service;

import io.github.muhammadredin.tokonyadiaapi.dto.request.ForgotPasswordRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.LoginRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.PasswordResetRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.request.UserAccountRequest;
import io.github.muhammadredin.tokonyadiaapi.dto.response.ForgotPasswordResponse;
import io.github.muhammadredin.tokonyadiaapi.dto.response.LoginResponse;
import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

public interface UserAccountService extends UserDetailsService {
    void createUserAccount(UserAccountRequest userAccount);

    UserAccount getOne(String id);

    UserAccount getOneByEmail(String email);

    ForgotPasswordResponse createPasswordResetRequest(ForgotPasswordRequest request);

    void passwordReset(PasswordResetRequest request);
}
