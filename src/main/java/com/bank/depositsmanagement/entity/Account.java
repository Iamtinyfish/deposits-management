package com.bank.depositsmanagement.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
@Getter
@Setter
public class Account implements UserDetails {
	@Id
	@GeneratedValue
	private Long id;

	@Pattern(message = "Tên tài khoản gồm 5-20 kí tự chữ, số, gạch dưới và phải bắt đầu bằng chữ, kết thúc bằng chữ hoặc số", regexp = "^[a-zA-Z](_|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$")
	@Column(nullable = false)
	private String username;

	@Pattern(message = "Mật khẩu phải có ít nhất 8 kí tự và không chứa khoảng trắng", regexp = "[^\\s\\n]{8,}")
	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String role;

	@Column(nullable = false)
	private boolean isActive;
	
	@OneToOne(mappedBy = "account")
	private Employee employee;

	@Override
	public List<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> grantList = new ArrayList<>();
		GrantedAuthority authority = new SimpleGrantedAuthority(role);
		grantList.add(authority);
		return grantList;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.isActive;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.isActive;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.isActive;
	}

	@Override
	public boolean isEnabled() {
		return this.isActive;
	}
}
