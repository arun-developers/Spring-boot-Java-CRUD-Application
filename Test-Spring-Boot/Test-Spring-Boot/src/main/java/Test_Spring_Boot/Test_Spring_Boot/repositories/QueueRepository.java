package Test_Spring_Boot.Test_Spring_Boot.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Test_Spring_Boot.Test_Spring_Boot.entities.Queue;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
	List<Queue> findByStatus(Queue.Status status);

	Optional<Queue> findByTaskName(String taskName);
}
