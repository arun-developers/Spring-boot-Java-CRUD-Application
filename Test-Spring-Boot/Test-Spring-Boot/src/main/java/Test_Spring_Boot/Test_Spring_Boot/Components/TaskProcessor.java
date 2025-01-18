package Test_Spring_Boot.Test_Spring_Boot.Components;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import Test_Spring_Boot.Test_Spring_Boot.entities.Queue;
import Test_Spring_Boot.Test_Spring_Boot.repositories.QueueRepository;
import Test_Spring_Boot.Test_Spring_Boot.services.QueueService;
import jakarta.transaction.Transactional;

@Component
public class TaskProcessor {

	@Autowired
	private QueueRepository queueRepository;

	@Autowired
	private QueueService queueService;

	@Transactional
	public void processPendingTasks() {
		List<Queue> tasks = queueRepository.findByStatus(Queue.Status.PENDING);
		for (Queue task : tasks) {
			try {
				task.setStatus(Queue.Status.PROCESSING);
				queueRepository.save(task);
				queueService.processTask(task, task.getTaskName());
				task.setStatus(Queue.Status.COMPLETED);
				queueRepository.save(task);
			} catch (Exception e) {
				task.setStatus(Queue.Status.FAILED);
				queueRepository.save(task);
				e.printStackTrace();
			}
		}
	}
}
