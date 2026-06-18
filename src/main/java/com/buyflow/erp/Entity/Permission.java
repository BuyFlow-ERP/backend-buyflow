package com.buyflow.erp.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PERMISSIONS")
@SequenceGenerator(
        name = "PERMISSIONS_SEQ_GENERATOR",
        sequenceName = "SEQ_PERMISSIONS",
        allocationSize = 1
)
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERMISSIONS_SEQ_GENERATOR")
    @Column(name = "PERMISSION_ID")
    private Long permissionId;

    @Column(name = "PERMISSION_CODE", length = 50, nullable = false, unique = true)
    private String permissionCode;

    @Column(name = "PERMISSION_NAME", length = 50, nullable = false)
    private String permissionName;

    @Column(name = "PERMISSION_GROUP", length = 50)
    private String permissionGroup;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @Column(name = "USE_YN", length = 1, nullable = false)
    private String useYn = "Y";

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}

