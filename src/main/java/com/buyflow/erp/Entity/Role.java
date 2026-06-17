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
@Table(name = "ROLES")
@SequenceGenerator(
        name = "ROLES_SEQ_GENERATOR",
        sequenceName = "SEQ_ROLES",
        allocationSize = 1
)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROLES_SEQ_GENERATOR")
    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "ROLE_CODE", length = 30, nullable = false, unique = true)
    private String roleCode;

    @Column(name = "ROLE_NAME", length = 50, nullable = false)
    private String roleName;

    @Column(name = "ROLE_GROUP", length = 30)
    private String roleGroup;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder = 0;

    @Column(name = "USE_YN", length = 1, nullable = false)
    private String useYn = "Y";

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}

