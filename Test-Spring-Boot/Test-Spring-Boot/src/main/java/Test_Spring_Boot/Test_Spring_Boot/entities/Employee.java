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
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 18, message = "Age must be at least 18")
    @Column(nullable = false)
    private int age;

    @Column(name = "employee_id", unique = true)
    private String employeeId;

    @NotNull(message = "Name is required")
    @NotEmpty(message = "Name cannot be empty")
    @Column(nullable = false)
    private String name;

    @Email(message = "Invalid email format")
    @NotNull(message = "Email is required")
    @NotEmpty(message = "Email cannot be empty")
    @Column(nullable = false)
    private String email;

    @NotNull(message = "Gender is required")
    @NotEmpty(message = "Gender cannot be empty")
    @Column(nullable = false)
    private String gender;

    @NotNull(message = "Position is required")
    @NotEmpty(message = "Position cannot be empty")
    @Column(nullable = false)
    private String position;

    @NotNull(message = "Location is required")
    @NotEmpty(message = "Location cannot be empty")
    @Column(nullable = false)
    private String location;

    @NotNull(message = "Experience is required")
    @NotEmpty(message = "Experience cannot be empty")
    @Column(nullable = false)
    private String experience;

    @Column(name = "created_by_user")
    private String createdByUser;

    @Column(name = "created_by_user_id")
    private String createdByUserId;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp updatedAt;

    @PostPersist
    private void generateEmployeeId() {
        LocalDate now = LocalDate.now();
        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyMM"));
        String formattedId = String.format("%04d", this.id);
        this.employeeId = "EMP-" + yearMonth + "-" + formattedId;
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

