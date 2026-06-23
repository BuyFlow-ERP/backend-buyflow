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
        name = "DEPARTMENT_ROLE_ASSIGN_RULES",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_DEPT_ROLE_ASSIGN_RULE",
                columnNames = {"DEPARTMENT_NAME", "ROLE_CODE"}
        )
)
@SequenceGenerator(
        name = "DEPARTMENT_ROLE_ASSIGN_RULES_SEQ_GENERATOR",
        sequenceName = "SEQ_DEPT_ROLE_ASSIGN_RULES",
        allocationSize = 1
)
public class DepartmentRoleAssignmentRule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEPARTMENT_ROLE_ASSIGN_RULES_SEQ_GENERATOR")
    @Column(name = "RULE_ID")
    private Long ruleId;

    @Column(name = "DEPARTMENT_NAME", length = 100, nullable = false)
    private String departmentName;

    @Column(name = "ROLE_CODE", length = 30, nullable = false)
    private String roleCode;

    @Column(name = "BUSINESS_AREA", length = 50)
    private String businessArea;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder = 0;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "USE_YN", length = 1, nullable = false)
    private String useYn = "Y";

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
