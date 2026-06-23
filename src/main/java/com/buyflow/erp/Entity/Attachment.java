package com.buyflow.erp.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ATTACHMENT")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {
	
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "attachment_seq"
	)
	@SequenceGenerator(
		name = "attachment_seq",
		sequenceName = "SEQ_ATTACHMENT",
		allocationSize = 1
	)
	@Column(name = "ATTACHMENT_ID")
	private Long attachmentId;
	
	@Column(name = "ORIGINAL_NAME", nullable = false)
	private String originalName;
	
	@Column(name = "SAVED_NAME", nullable = false)
	private String savedName;
	
	@Column(name = "FILE_PATH", nullable = false)
	private String filePath;
	
	@Column(name = "FILE_SIZE")
	private Long fileSize;
	
	@Column(name = "EXTENSION", length = 20)
	private String extension;
	
	@Column(name = "UPLOADED_BY", length = 50)
	private String uploadedBy;
	
	@Column(name = "UPLOADED_AT")
	private LocalDateTime uploadedAt;

	@Column(name = "REQUEST_ID")
  private Long requestId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private Users user;
}
