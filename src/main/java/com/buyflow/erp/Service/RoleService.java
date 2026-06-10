package com.buyflow.erp.Service;

import com.buyflow.erp.Dto.RoleResponse;
import com.buyflow.erp.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;

    public List<RoleResponse> findAll() {
        return roleRepository.findByUseYnOrderBySortOrderAscRoleCodeAsc("Y")
                .stream()
                .map(RoleResponse::from)
                .toList();
    }
}

