package Test_Spring_Boot.Test_Spring_Boot.entities;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class UserRegister {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String userId;

	@NotNull(message = "Name is required")
	@NotEmpty(message = "Name cannot be empty")
	private String name;

	@Email(message = "Invalid email format")
	@NotNull(message = "Email is required")
	@NotEmpty(message = "Email cannot be empty")
	private String email;

	@NotNull(message = "Contact is required")
	@NotEmpty(message = "Contact cannot be empty")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "The contact must be a 10-digit number starting with 6, 7, 8, or 9")
	private String contact;

	@NotNull(message = "Password is required")
	@NotEmpty(message = "Password cannot be empty")
	private String password;

	@NotNull(message = "Role is required")
	@NotEmpty(message = "Role cannot be empty")
	private String role;

	Timestamp createdAt;
	Timestamp updatedAt;

	@PostPersist
	private void generateUserId() {
		LocalDate now = LocalDate.now();
		String yearMonth = now.format(DateTimeFormatter.ofPattern("yyMM"));
		String formattedId = String.format("%04d", this.id);
		this.userId = "USER-" + yearMonth + "-" + formattedId;
	}
}
