package com.buyflow.erp.Entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supplier_seq")
    @SequenceGenerator(name = "supplier_seq", sequenceName = "SEQ_SUPPLIER", allocationSize = 1)
    @Column(name = "SUPPLIER_ID")
	private Long supplierId;
	
	@Column(name = "SUPPLIER_NAME")
	private String supplierName;
	
	@Column(name = "CONTACT")
	private String contact;
	
	@Column(name = "ADDRESS")
	private String address;
	
	@Column(name = "MANAGER")
	private String manager;
	
	@Column(name = "CREATED_AT")
	private LocalDate createdAt;

}
