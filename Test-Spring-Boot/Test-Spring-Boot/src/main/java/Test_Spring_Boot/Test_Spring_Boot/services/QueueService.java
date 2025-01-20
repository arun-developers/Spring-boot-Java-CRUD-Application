package Test_Spring_Boot.Test_Spring_Boot.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import Test_Spring_Boot.Test_Spring_Boot.entities.Queue;
import Test_Spring_Boot.Test_Spring_Boot.helpers.Messages;
import Test_Spring_Boot.Test_Spring_Boot.repositories.QueueRepository;

@Service
public class QueueService {

	@Autowired
	private EmailService emailService;

	@Autowired
	private QueueRepository queueRepository;

	// Method to create new queue
	public void addTask(String taskName, String payload) {
		Queue task = new Queue();
		task.setTaskName(taskName);
		task.setPayload(payload);
		task.setStatus(Queue.Status.PENDING);
		queueRepository.save(task);
	}

	// Method to find All queues
	public List<Queue> findAllQueues() {
		return queueRepository.findAll();
	}

	// Method to find queue by task name
	public Optional<Queue> findByTaskName(String taskName) {
		return queueRepository.findByTaskName(taskName);
	}

	public void processTask(Queue task, String taskName) {

		switch (taskName) {
			case "employeeOnboardingNotification":
				handleEmployeeOnboarding(task);
				break;
			case "employeeUpdateNotification":
				handleUpdateEmployeeDetails(task);
				break;
			case "generateResetPasswordNotification":
				handleGenerateResetPasswordLink(task);
				break;
			case "changePasswordNotification":
				handleChangePassword(task);
				break;
			case "resetPasswordNotification":
				handleResetPassword(task);
				break;
			default:
				throw new IllegalArgumentException("Unknown task: " + taskName);
		}
	}

	private void handleEmployeeOnboarding(Queue task) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> payloadMap = objectMapper.readValue(task.getPayload(),
					Map.class);

			// Access individual key-value pairs
			String name = (String) payloadMap.get("name");
			String employeeId = (String) payloadMap.get("employeeId");
			String position = (String) payloadMap.get("position");
			String emailId = (String) payloadMap.get("emailId");
			String gender = (String) payloadMap.get("gender");
			String location = (String) payloadMap.get("location");
			String experience = (String) payloadMap.get("experience");

			Map<String, Object> welcomeMessageObj = new HashMap<>();
			welcomeMessageObj.put("name", name);
			welcomeMessageObj.put("employeeId", employeeId);
			welcomeMessageObj.put("position", position);
			welcomeMessageObj.put("emailId", emailId);
			welcomeMessageObj.put("gender", gender);
			welcomeMessageObj.put("location", location);
			welcomeMessageObj.put("experience", experience);

			String welcomeSubject = Messages.EMPLOYEE_WELCOME_SUBJECT;
			String welcomeMessage = Messages.prepareMessage("WELCOME", welcomeMessageObj);

			emailService.sendHtmlEmail(emailId, welcomeSubject, welcomeMessage);

		} catch (Exception e) {
			throw new RuntimeException("Failed to process task payload", e);
		}
	}

	private void handleUpdateEmployeeDetails(Queue task) {
		try {

			ObjectMapper objectMapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> payloadMap = objectMapper.readValue(task.getPayload(), Map.class);

			String name = (String) payloadMap.get("name");
			String emailId = (String) payloadMap.get("emailId");
			String employeeId = (String) payloadMap.get("employeeId");
			String position = (String) payloadMap.get("position");
			String gender = (String) payloadMap.get("gender");
			String location = (String) payloadMap.get("location");
			String experience = (String) payloadMap.get("experience");

			Map<String, Object> updateMessageObj = new HashMap<>();

			updateMessageObj.put("name", name);
			updateMessageObj.put("emailId", emailId);
			updateMessageObj.put("employeeId", employeeId);
			updateMessageObj.put("position", position);
			updateMessageObj.put("gender", gender);
			updateMessageObj.put("location", location);
			updateMessageObj.put("experience", experience);

			String updateSubject = Messages.EMPLOYEE_UPDATE_SUBJECT;
			String updateMessage = Messages.prepareMessage("UPDATE", updateMessageObj);

			emailService.sendHtmlEmail(emailId, updateSubject, updateMessage);

		} catch (Exception e) {
			throw new RuntimeException("Failed to process task payload", e);
		}
	}

	private void handleGenerateResetPasswordLink(Queue task) {
		try {

			ObjectMapper objectMapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> payloadMap = objectMapper.readValue(task.getPayload(), Map.class);

			String name = (String) payloadMap.get("name");
			String emailId = (String) payloadMap.get("emailId");
			String url = (String) payloadMap.get("url");

			Map<String, Object> forgotPasswordMessageObj = new HashMap<>();

			forgotPasswordMessageObj.put("name", name);
			forgotPasswordMessageObj.put("emailId", emailId);
			forgotPasswordMessageObj.put("url", url);

			String forgotPasswordSubject = Messages.GENERATE_RESET_PASSWORD_SUBJECT;
			String forgotPasswordMessage = Messages.prepareMessage("GENERATE_RESET_PASSWORD_LINK",
					forgotPasswordMessageObj);

			emailService.sendHtmlEmail(emailId, forgotPasswordSubject, forgotPasswordMessage);

		} catch (Exception e) {
			throw new RuntimeException("Failed to process task payload", e);
		}
	}

	private void handleChangePassword(Queue task) {
		try {

			ObjectMapper objectMapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> payloadMap = objectMapper.readValue(task.getPayload(), Map.class);

			String name = (String) payloadMap.get("name");
			String emailId = (String) payloadMap.get("emailId");
			String userId = (String) payloadMap.get("userId");
			String role = (String) payloadMap.get("role");
			String contact = (String) payloadMap.get("contact");

			Map<String, Object> changePasswordObj = new HashMap<>();

			changePasswordObj.put("name", name);
			changePasswordObj.put("emailId", emailId);
			changePasswordObj.put("userId", userId);
			changePasswordObj.put("role", role);
			changePasswordObj.put("contact", contact);

			String changePasswordSubject = Messages.CHANGE_USER_PASSWORD_SUBJECT;
			String changePasswordMessage = Messages.prepareMessage("CHANGE_PASSWORD", changePasswordObj);

			emailService.sendHtmlEmail(emailId, changePasswordSubject, changePasswordMessage);

		} catch (Exception e) {
			throw new RuntimeException("Failed to process task payload", e);
		}
	}

	private void handleResetPassword(Queue task) {
		try {

			ObjectMapper objectMapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> payloadMap = objectMapper.readValue(task.getPayload(), Map.class);

			String name = (String) payloadMap.get("name");
			String emailId = (String) payloadMap.get("emailId");
			String userId = (String) payloadMap.get("userId");

			Map<String, Object> resetPasswordObj = new HashMap<>();

			resetPasswordObj.put("name", name);
			resetPasswordObj.put("emailId", emailId);
			resetPasswordObj.put("userId", userId);

			String resetPasswordSubject = Messages.RESET_PASSWORD_SUBJECT;
			String resetPasswordMessage = Messages.prepareMessage("RESET_PASSWORD", resetPasswordObj);

			emailService.sendHtmlEmail(emailId, resetPasswordSubject, resetPasswordMessage);
		} catch (Exception e) {
			throw new RuntimeException("Failed to process task payload", e);
		}
	}

}
