package com.bank.depositsmanagement.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "branch")
@Getter
@Setter
public class Branch {

	@Id
	@GeneratedValue
	private Long id;

	@NotBlank
	private String branchName;

	@NotBlank
	private String address;

	@OneToMany(mappedBy = "branch")
	private Set<Employee> employeeSet;
}
