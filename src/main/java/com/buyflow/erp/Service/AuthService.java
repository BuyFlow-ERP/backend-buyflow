package com.buyflow.erp.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.buyflow.erp.Common.BusinessException;
import com.buyflow.erp.Common.ErrorCode;
import com.buyflow.erp.Common.VerificationPurpose;
import com.buyflow.erp.Dto.FindLoginIdCodeRequest;
import com.buyflow.erp.Dto.FindLoginIdRequest;
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
import com.buyflow.erp.Entity.EmailVerificationCode;
import com.buyflow.erp.Entity.PasswordResetToken;
import com.buyflow.erp.Entity.Role;
import com.buyflow.erp.Entity.User;
import com.buyflow.erp.Entity.UserRole;
import com.buyflow.erp.Repository.AuthUserRepository;
import com.buyflow.erp.Repository.RoleRepository;
import com.buyflow.erp.Repository.UserRoleRepository;
import com.buyflow.erp.Security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RbacQueryService rbacQueryService;
    private final DepartmentAuthorizationService departmentAuthorizationService;
    private final VerificationCodeService verificationCodeService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserResponse signup(SignupRequest request) {
        String loginId = request.loginId().trim();
        String userName = request.userName().trim();
        String email = request.email().trim().toLowerCase();
        String phone = normalizeOptionalText(request.phone());
        String departmentName = request.departmentName().trim();
        String positionName = normalizeOptionalText(request.positionName());
        String jobRank = request.jobRank().trim();

        if (userRepository.existsByLoginId(loginId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setLoginId(loginId);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setUserName(userName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setDepartmentName(departmentName);
        user.setPositionName(StringUtils.hasText(positionName) ? positionName : "담당자");
        user.setJobRank(jobRank);
        user.setStatus("ACTIVE");
        user.setUseYn("Y");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User savedUser = userRepository.save(user);
        departmentAuthorizationService.setAuthorized(savedUser, false);

        Role viewerRole = roleRepository.findByRoleCodeAndUseYn("VIEWER", "Y")
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_ERROR,
                        "Default VIEWER role is not configured."
                ));

        UserRole userRole = new UserRole();
        userRole.setUserId(savedUser.getUserId());
        userRole.setRoleId(viewerRole.getRoleId());
        userRole.setCreatedAt(now);
        userRoleRepository.save(userRole);

        return UserResponse.from(savedUser);
    }

    private String normalizeOptionalText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_LOGIN));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_LOGIN);
        }

        if (!"ACTIVE".equals(user.getStatus()) || !"Y".equals(user.getUseYn())) {
            throw new BusinessException(ErrorCode.INACTIVE_USER);
        }

        List<String> roles = rbacQueryService.findRoleCodesByUserId(user.getUserId());
        List<String> permissions = rbacQueryService.findPermissionCodesByUserId(user.getUserId());
        String accessToken = jwtTokenProvider.generateAccessToken(user, roles, permissions);

        user.setLastLoginAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return new LoginResponse(accessToken, UserResponse.from(user), roles, permissions);
    }

    @Transactional(readOnly = true)
    public MeResponse getMe(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        List<String> roles = rbacQueryService.findRoleCodesByUserId(user.getUserId());
        List<String> permissions = rbacQueryService.findPermissionCodesByUserId(user.getUserId());

        return new MeResponse(UserResponse.from(user), roles, permissions);
    }

    

    // ↓↓↓ 여기에 새 메서드 추가 ↓↓↓
    @Transactional(readOnly = true)
    public FindLoginIdResponse findLoginId(FindLoginIdRequest request) {
        User user = userRepository.findFirstByUserNameAndEmailAndUseYn(
                        request.userName().trim(),
                        request.email().trim().toLowerCase(),
                        "Y"
                )
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND, "일치하는 사용자 정보가 없습니다."));

        return new FindLoginIdResponse(user.getLoginId());
    }
    // ↑↑↑ 여기까지 추가 ↑↑↑

    

    @Transactional
    public VerificationCodeResponse requestFindLoginIdCode(FindLoginIdCodeRequest request) {
        User user = userRepository.findFirstByUserNameAndEmailAndPhoneAndUseYn(
                        request.userName(),
                        request.email(),
                        request.phone(),
                        "Y"
                )
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "No matching user information was found."));

        return verificationCodeService.issue(VerificationPurpose.FIND_LOGIN_ID, user);
    }

    @Transactional
    public FindLoginIdResponse verifyFindLoginIdCode(VerificationCodeVerifyRequest request) {
        EmailVerificationCode verificationCode = verificationCodeService.verify(
                VerificationPurpose.FIND_LOGIN_ID,
                request.verificationId(),
                request.code()
        );

        User user = userRepository.findById(verificationCode.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "User not found."));

        verificationCodeService.consume(verificationCode);
        return new FindLoginIdResponse(user.getLoginId());
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        User user = userRepository.findFirstByLoginIdAndEmailAndUseYn(
                        request.loginId(),
                        request.email(),
                        "Y"
                )
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "No matching user information was found."));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public VerificationCodeResponse requestPasswordResetCode(PasswordResetCodeRequest request) {
        User user = userRepository.findFirstByLoginIdAndUserNameAndEmailAndPhoneAndUseYn(
                        request.loginId(),
                        request.userName(),
                        request.email(),
                        request.phone(),
                        "Y"
                )
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "No matching user information was found."));

        return verificationCodeService.issue(VerificationPurpose.RESET_PASSWORD, user);
    }

    @Transactional
    public PasswordResetVerifyResponse verifyPasswordResetCode(VerificationCodeVerifyRequest request) {
        EmailVerificationCode verificationCode = verificationCodeService.verify(
                VerificationPurpose.RESET_PASSWORD,
                request.verificationId(),
                request.code()
        );

        PasswordResetVerifyResponse response = passwordResetTokenService.issue(verificationCode.getUserId());
        verificationCodeService.consume(verificationCode);
        return response;
    }

    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        PasswordResetToken resetToken = passwordResetTokenService.verify(
                request.resetTokenId(),
                request.resetToken()
        );

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "User not found."));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        passwordResetTokenService.consume(resetToken);
    }
}
