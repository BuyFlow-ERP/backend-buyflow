package com.buyflow.erp.Service;

import com.buyflow.erp.Common.BusinessException;
import com.buyflow.erp.Common.ErrorCode;
import com.buyflow.erp.Common.VerificationPurpose;
import com.buyflow.erp.Dto.FindLoginIdCodeRequest;
import com.buyflow.erp.Dto.FindLoginIdResponse;
import com.buyflow.erp.Dto.LoginRequest;
import com.buyflow.erp.Dto.LoginResponse;
import com.buyflow.erp.Dto.MeResponse;
import com.buyflow.erp.Dto.PasswordResetCodeRequest;
import com.buyflow.erp.Dto.PasswordResetConfirmRequest;
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
import com.buyflow.erp.Repository.RoleRepository;
import com.buyflow.erp.Repository.AuthUserRepository;
import com.buyflow.erp.Repository.UserRoleRepository;
import com.buyflow.erp.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;

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
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setLoginId(request.loginId());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setUserName(request.userName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setDepartmentName(request.departmentName());
        user.setPositionName(
                StringUtils.hasText(request.positionName()) ? request.positionName() : "담당자");
        user.setJobRank(
                StringUtils.hasText(request.jobRank()) ? request.jobRank().trim() : "사원");
        user.setStatus("PENDING");
        user.setUseYn("Y");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User savedUser = userRepository.save(user);
        departmentAuthorizationService.ensureDefaultAuthorization(savedUser);

        roleRepository.findByRoleCodeAndUseYn("VIEWER", "Y")
                .map(Role::getRoleId)
                .ifPresent(roleId -> {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(savedUser.getUserId());
                    userRole.setRoleId(roleId);
                    userRole.setCreatedAt(now);
                    userRoleRepository.save(userRole);
                });

        return UserResponse.from(savedUser);
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
