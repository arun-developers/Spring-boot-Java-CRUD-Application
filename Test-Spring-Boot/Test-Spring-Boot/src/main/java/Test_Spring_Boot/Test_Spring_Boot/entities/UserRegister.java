package Test_Spring_Boot.Test_Spring_Boot.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_register")
public class UserRegister {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", unique = true)
	private String userId;

	@NotNull(message = "Name is required")
	@NotEmpty(message = "Name cannot be empty")
	@Column(nullable = false)
	private String name;

	@Email(message = "Invalid email format")
	@NotNull(message = "Email is required")
	@NotEmpty(message = "Email cannot be empty")
	@Column(nullable = false, unique = true)
	private String email;

	@NotNull(message = "Contact is required")
	@NotEmpty(message = "Contact cannot be empty")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "The contact must be a 10-digit number starting with 6, 7, 8, or 9")
	@Column(nullable = false)
	private String contact;

	@NotNull(message = "Password is required")
	@NotEmpty(message = "Password cannot be empty")
	@Column(nullable = false)
	private String password;

	@NotNull(message = "Role is required")
	@NotEmpty(message = "Role cannot be empty")
	@Column(nullable = false)
	private String role;

	@Column(name = "created_at", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp createdAt;

	@Column(name = "updated_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp updatedAt;

	@PostPersist
	private void generateUserId() {
		LocalDate now = LocalDate.now();
		String yearMonth = now.format(DateTimeFormatter.ofPattern("yyMM"));
		String formattedId = String.format("%04d", this.id);
		this.userId = this.role.replaceAll("\\s+", "").toUpperCase() + "-" + yearMonth + "-" + formattedId;
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = new Timestamp(System.currentTimeMillis());
		this.updatedAt = new Timestamp(System.currentTimeMillis());
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = new Timestamp(System.currentTimeMillis());
	}
}
