package Test_Spring_Boot.Test_Spring_Boot.Components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CustomTaskScheduler {

	@Autowired
	private TaskProcessor taskProcessor;

	@Scheduled(fixedRate = 5000) // Run every 5 seconds
	public void processTasks() {
		System.out.println("Task Scheduler executed successfully");
		taskProcessor.processPendingTasks();
	}
}
