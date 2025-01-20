package Test_Spring_Boot.Test_Spring_Boot.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Queue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String queueId;

	@Column(name = "task_name", nullable = false)
	private String taskName;

	@Column(name = "payload", nullable = false)
	private String payload;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status = Status.PENDING;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", updatable = true)
	private LocalDateTime updatedAt;

	public enum Status {
		PENDING,
		PROCESSING,
		COMPLETED,
		FAILED
	}

	@PostPersist
	private void generateQueueId() {
		LocalDate now = LocalDate.now();
		String yearMonth = now.format(DateTimeFormatter.ofPattern("yyMM"));
		String formattedId = String.format("%04d", this.id);
		this.queueId = "QUEUE-" + yearMonth + "-" + formattedId;
	}
}
