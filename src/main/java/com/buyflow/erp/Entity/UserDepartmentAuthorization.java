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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "USER_DEPARTMENT_AUTHORIZATIONS",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_USER_DEPT_AUTH_USER",
                columnNames = {"USER_ID"}
        )
)
@SequenceGenerator(
        name = "USER_DEPARTMENT_AUTHORIZATIONS_SEQ_GENERATOR",
        sequenceName = "SEQ_USER_DEPT_AUTHS",
        allocationSize = 1
)
public class UserDepartmentAuthorization {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_DEPARTMENT_AUTHORIZATIONS_SEQ_GENERATOR")
    @Column(name = "USER_DEPARTMENT_AUTH_ID")
    private Long userDepartmentAuthId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "DEPARTMENT_NAME", length = 100, nullable = false)
    private String departmentName;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "AUTHORIZED_YN", length = 1, nullable = false)
    private String authorizedYn = "Y";

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
