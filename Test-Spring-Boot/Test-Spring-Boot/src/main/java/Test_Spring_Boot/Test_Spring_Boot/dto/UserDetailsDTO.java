package Test_Spring_Boot.Test_Spring_Boot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO {
	private Long id;
	private String userId;
	private String initials;
	private String username;
	private String contact;
	private String name;
	private String role;
	private boolean enabled;
	private boolean credentialsNonExpired;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
}
