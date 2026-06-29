package com.buyflow.erp.Controller;

import com.buyflow.erp.Common.ApiResponse;
import com.buyflow.erp.Dto.FindLoginIdCodeRequest;
import com.buyflow.erp.Dto.FindLoginIdResponse;
import com.buyflow.erp.Dto.LoginRequest;
import com.buyflow.erp.Dto.LoginResponse;
import com.buyflow.erp.Dto.MeResponse;
import com.buyflow.erp.Dto.PasswordResetCodeRequest;
import com.buyflow.erp.Dto.PasswordResetConfirmRequest;
import com.buyflow.erp.Dto.PasswordResetRequest;
import com.buyflow.erp.Dto.PasswordResetVerifyResponse;
import com.buyflow.erp.Dto.SignupRequest;
import com.buyflow.erp.Dto.UserResponse;
import com.buyflow.erp.Dto.VerificationCodeResponse;
import com.buyflow.erp.Dto.VerificationCodeVerifyRequest;
import com.buyflow.erp.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.buyflow.erp.Dto.FindLoginIdRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.success("Signup request completed.", authService.signup(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("Login successful.", authService.login(request));
    }
    
    @PostMapping("/find-login-id")
public ApiResponse<FindLoginIdResponse> findLoginId(
        @Valid @RequestBody FindLoginIdRequest request
    ) {
    return ApiResponse.success("Login ID found.", authService.findLoginId(request));
    }

    @PostMapping("/find-login-id/code")
    public ApiResponse<VerificationCodeResponse> requestFindLoginIdCode(
            @Valid @RequestBody FindLoginIdCodeRequest request
    ) {
        return ApiResponse.success("Verification code issued.", authService.requestFindLoginIdCode(request));
    }

    @PostMapping("/find-login-id/verify")
    public ApiResponse<FindLoginIdResponse> verifyFindLoginIdCode(
            @Valid @RequestBody VerificationCodeVerifyRequest request
    ) {
        return ApiResponse.success("Login ID found.", authService.verifyFindLoginIdCode(request));
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success("Password reset completed.");
    }

    @PostMapping("/reset-password/code")
    public ApiResponse<VerificationCodeResponse> requestPasswordResetCode(
            @Valid @RequestBody PasswordResetCodeRequest request
    ) {
        return ApiResponse.success("Verification code issued.", authService.requestPasswordResetCode(request));
    }

    @PostMapping("/reset-password/verify")
    public ApiResponse<PasswordResetVerifyResponse> verifyPasswordResetCode(
            @Valid @RequestBody VerificationCodeVerifyRequest request
    ) {
        return ApiResponse.success("Password reset verified.", authService.verifyPasswordResetCode(request));
    }

    @PostMapping("/reset-password/confirm")
    public ApiResponse<Void> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        authService.confirmPasswordReset(request);
        return ApiResponse.success("Password reset completed.");
    }

    @GetMapping("/me")
    public ApiResponse<MeResponse> me(Authentication authentication) {
        return ApiResponse.success("Current user loaded.", authService.getMe(authentication.getName()));
    }
}
