package com.buyflow.erp.Controller;

import com.buyflow.erp.Common.ApiResponse;
import com.buyflow.erp.Dto.FindLoginIdRequest;
import com.buyflow.erp.Dto.FindLoginIdResponse;
import com.buyflow.erp.Dto.LoginRequest;
import com.buyflow.erp.Dto.LoginResponse;
import com.buyflow.erp.Dto.MeResponse;
import com.buyflow.erp.Dto.ResetPasswordRequest;
import com.buyflow.erp.Dto.SignupRequest;
import com.buyflow.erp.Dto.UserResponse;
import com.buyflow.erp.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
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
    public ApiResponse<FindLoginIdResponse> findLoginId(@Valid @RequestBody FindLoginIdRequest request) {
        return ApiResponse.success("Login ID found.", authService.findLoginId(request));
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success("Password reset completed.");
    }

    @GetMapping("/me")
    public ApiResponse<MeResponse> me(Authentication authentication) {
        return ApiResponse.success("Current user loaded.", authService.getMe(authentication.getName()));
    }
}
