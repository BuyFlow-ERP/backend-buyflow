package com.buyflow.erp.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "USERS")
@NoArgsConstructor
public class Users {

	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "users_seq"
	)
	@SequenceGenerator(
			name = "users_seq",
			sequenceName = "SEQ_USERS",
			allocationSize = 1
	)
	@Column(name = "USER_ID")
	private Long userId;
	
	@Column(name = "USER_NAME")
	private String userName;
	
	@Column(name = "LOGIN_ID")
	private String loginId;
}
