package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.PermissionResponse;
import com.buyflow.erp.Repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<PermissionResponse> findAll() {
        return permissionRepository.findByUseYnOrderByPermissionGroupAscPermissionCodeAsc("Y")
                .stream()
                .map(PermissionResponse::from)
                .toList();
    }
}

