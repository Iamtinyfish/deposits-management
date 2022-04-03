package com.bank.depositsmanagement.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "position")
@Getter
@Setter
public class Position {
	@Id
	@GeneratedValue
	private Long id;
	
	@NotBlank
	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "position", orphanRemoval = true)
	private Set<Employee> employeeSet = new java.util.LinkedHashSet<>();
}
