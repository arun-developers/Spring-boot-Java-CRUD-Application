package Test_Spring_Boot.Test_Spring_Boot.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import Test_Spring_Boot.Test_Spring_Boot.entities.UserRegister;

public class CustomUserDetails implements UserDetails {
	private Long id;
	private String email;
	private String password;
	private String contact;
	private String name;
	private String role;
	private List<GrantedAuthority> authorities;

	public CustomUserDetails(UserRegister user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.contact = user.getContact();
		this.name = user.getName();
		this.role = user.getRole();
		this.authorities = new ArrayList<>();

		if (user.getRole() != null) {
			this.authorities.add(new SimpleGrantedAuthority(getRole().toUpperCase()));
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public String getContact() {
		return contact;
	}

	public String getName() {
		return name;
	}

	public String getRole() {
		return role;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
