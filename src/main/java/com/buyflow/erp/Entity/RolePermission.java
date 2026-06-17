package com.buyflow.erp.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "ROLE_PERMISSIONS",
        uniqueConstraints = @UniqueConstraint(name = "UK_ROLE_PERMISSIONS_ROLE_PERMISSION", columnNames = {"ROLE_ID", "PERMISSION_ID"})
)
@SequenceGenerator(
        name = "ROLE_PERMISSIONS_SEQ_GENERATOR",
        sequenceName = "SEQ_ROLE_PERMISSIONS",
        allocationSize = 1
)
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROLE_PERMISSIONS_SEQ_GENERATOR")
    @Column(name = "ROLE_PERMISSION_ID")
    private Long rolePermissionId;

    @Column(name = "ROLE_ID", nullable = false)
    private Long roleId;

    @Column(name = "PERMISSION_ID", nullable = false)
    private Long permissionId;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
}

