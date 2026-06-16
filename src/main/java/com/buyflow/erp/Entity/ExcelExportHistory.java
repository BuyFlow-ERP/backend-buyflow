package com.buyflow.erp.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExcelExportHistory {
	
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE, 
		generator = "export_seq"
	)
	@SequenceGenerator(
		name = "export_seq",
		sequenceName = "SEQ_EXPORT",
		allocationSize = 1
	)
	@Column(name = "EXPORT_ID")
	private Long exportId;
	
	@ManyToOne
	@JoinColumn(name = "ATTACHMENT_ID")
	private Attachment attachment;
	
	@Column(name = "EXPORT_TYPE")
	private String exportType;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;
	
	@Column(name = "DOWNLOAD_ROW_COUNT")
	private Long downloadRowCount;
}
