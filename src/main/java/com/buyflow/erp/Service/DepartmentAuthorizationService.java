package com.buyflow.erp.Service;

import com.buyflow.erp.Entity.User;
import com.buyflow.erp.Entity.UserDepartmentAuthorization;
import com.buyflow.erp.Repository.UserDepartmentAuthorizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DepartmentAuthorizationService {

    private final UserDepartmentAuthorizationRepository userDepartmentAuthorizationRepository;

    @Transactional
    public void ensureDefaultAuthorization(User user) {
        String departmentName = normalizeText(user.getDepartmentName());
        if (!StringUtils.hasText(departmentName)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        UserDepartmentAuthorization authorization = userDepartmentAuthorizationRepository
                .findByUserId(user.getUserId())
                .orElseGet(() -> {
                    UserDepartmentAuthorization created = new UserDepartmentAuthorization();
                    created.setUserId(user.getUserId());
                    created.setAuthorizedYn("Y");
                    created.setCreatedAt(now);
                    return created;
                });

        authorization.setDepartmentName(departmentName);
        authorization.setUpdatedAt(now);
        userDepartmentAuthorizationRepository.save(authorization);
    }

    @Transactional
    public void setAuthorized(User user, boolean authorized) {
        String departmentName = normalizeText(user.getDepartmentName());
        if (!StringUtils.hasText(departmentName)) {
            return;
        }

        ensureDefaultAuthorization(user);

        UserDepartmentAuthorization authorization = userDepartmentAuthorizationRepository
                .findByUserId(user.getUserId())
                .orElseThrow();

        authorization.setDepartmentName(departmentName);
        authorization.setAuthorizedYn(authorized ? "Y" : "N");
        authorization.setUpdatedAt(LocalDateTime.now());
        userDepartmentAuthorizationRepository.save(authorization);
    }

    @Transactional(readOnly = true)
    public boolean isAuthorized(User user) {
        String departmentName = normalizeText(user.getDepartmentName());
        if (!StringUtils.hasText(departmentName)) {
            return false;
        }

        return userDepartmentAuthorizationRepository.findByUserId(user.getUserId())
                .filter(authorization -> "Y".equals(authorization.getAuthorizedYn()))
                .filter(authorization -> departmentName.equals(normalizeText(authorization.getDepartmentName())))
                .isPresent();
    }

    private String normalizeText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }
}
