package Test_Spring_Boot.Test_Spring_Boot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

	@Email(message = "Invalid email format")
	@NotNull(message = "Email is required")
	@NotEmpty(message = "Email cannot be empty")
	private String email;

	@NotNull(message = "Password is required")
	@NotEmpty(message = "Password cannot be empty")
	private String password;
}
