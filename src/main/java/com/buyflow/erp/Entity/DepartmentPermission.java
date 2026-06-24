package com.buyflow.erp.Entity;

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

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "DEPARTMENT_PERMISSIONS",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_DEPT_PERM_DEPT_PID",
                columnNames = {"DEPARTMENT_NAME", "PERMISSION_ID"}
        )
)
@SequenceGenerator(
        name = "DEPARTMENT_PERMISSIONS_SEQ_GENERATOR",
        sequenceName = "SEQ_DEPARTMENT_PERMISSIONS",
        allocationSize = 1
)
public class DepartmentPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEPARTMENT_PERMISSIONS_SEQ_GENERATOR")
    @Column(name = "DEPARTMENT_PERMISSION_ID")
    private Long departmentPermissionId;

    @Column(name = "DEPARTMENT_NAME", length = 100, nullable = false)
    private String departmentName;

    @Column(name = "PERMISSION_ID", nullable = false)
    private Long permissionId;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
}
