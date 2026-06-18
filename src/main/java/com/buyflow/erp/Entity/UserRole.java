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
        name = "USER_ROLES",
        uniqueConstraints = @UniqueConstraint(name = "UK_USER_ROLES_USER_ROLE", columnNames = {"USER_ID", "ROLE_ID"})
)
@SequenceGenerator(
        name = "USER_ROLES_SEQ_GENERATOR",
        sequenceName = "SEQ_USER_ROLES",
        allocationSize = 1
)
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ROLES_SEQ_GENERATOR")
    @Column(name = "USER_ROLE_ID")
    private Long userRoleId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "ROLE_ID", nullable = false)
    private Long roleId;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
}

