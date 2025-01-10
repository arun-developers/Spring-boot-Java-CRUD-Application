package Test_Spring_Boot.Test_Spring_Boot.entities;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Min(value = 18, message = "Age must be greater than or equal to 18")
	private int age;

	@NotNull(message = "Name is required")
	@NotEmpty(message = "Name cannot be empty")
	private String name;

	@NotNull(message = "Gender is required")
	@NotEmpty(message = "Gender cannot be empty")
	private String gender;

	@NotNull(message = "Position is required")
	@NotEmpty(message = "Position cannot be empty")
	private String position;

	@NotNull(message = "Location is required")
	@NotEmpty(message = "Location cannot be empty")
	private String location;

	@NotNull(message = "Experience is required")
	@NotEmpty(message = "Experience cannot be empty")
	private String experience;

	Timestamp createdAt;
	Timestamp updatedAt;
}
