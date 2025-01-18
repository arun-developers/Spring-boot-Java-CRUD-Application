package Test_Spring_Boot.Test_Spring_Boot.helpers;

import java.util.HashMap;
import java.util.Map;

public class Messages {

	public static final String EMPLOYEE_WELCOME_SUBJECT = "Welcome to the Team!";

	public static final String EMPLOYEE_WELCOME_MESSAGE = "<p>Hello <EMPLOYEENAME>,<br><br>" +
			"We are excited to have you on board. As part of our team, you'll have the opportunity to grow, collaborate, " +
			"and contribute to exciting projects. Please feel free to reach out to your manager or HR if you have any questions or need assistance.<br><br>" +
			"<table style=\"border-collapse: collapse; width: 100%; font-family: Arial, sans-serif;\">" +
			"  <tbody>" +
			"<TABLE_ROWS>" +
			"  </tbody>" +
			"</table><br>" +
			"Best regards,<br>" +
			"The EPM Team<br>" +
			"<strong>Contact Us:</strong><br>" +
			"Email: support@emp.com<br>" +
			"Phone: (123) 456-7890<br>" +
			"Website: <a href='https://arun-developers.github.io/' style='text-decoration: none; color: #DC3545;'>Visit Our Website</a><br><br>" +
			"We look forward to a successful journey together!" +
			"<p style='color: #DC3545;'>Note: This email is for developer testing purposes only!</p></p>";

	public static final String EMPLOYEE_UPDATE_SUBJECT = "Profile Update Confirmation";

	public static final String EMPLOYEE_UPDATE_MESSAGE = "<p>Hello <EMPLOYEENAME>,<br><br>" +
			"We are pleased to inform you that your profile details have been successfully updated." +
			"If you have any questions or require assistance, please do not hesitate to contact your manager or the HR department.<br><br>" +
			"<table style=\"border-collapse: collapse; width: 100%; font-family: Arial, sans-serif;\">" +
			"  <tbody>" +
			"<TABLE_ROWS>" +
			"  </tbody>" +
			"</table><br>" +
			"Best regards,<br>" +
			"The EPM Team<br>" +
			"<strong>Contact Us:</strong><br>" +
			"Email: support@emp.com<br>" +
			"Phone: (123) 456-7890<br>" +
			"Website: <a href='https://arun-developers.github.io/' style='text-decoration: none; color: #DC3545;'>Visit Our Website</a><br><br>" +
			"We look forward to a successful journey together!" +
			"<p style='color: #DC3545;'>Note: This email is for developer testing purposes only!</p></p>";

	public static final String CHANGE_USER_PASSWORD_SUBJECT = "Password changed successfully!";

	public static final String CHANGE_USER_PASSWORD_MESSAGE = "<p>Hello <EMPLOYEENAME>,<br><br>" +
			"We are pleased to inform you that your password have been successfully changed." +
			"If you have any questions or require assistance, please do not hesitate to contact your manager or the HR department.<br><br>" +
			"<table style=\"border-collapse: collapse; width: 100%; font-family: Arial, sans-serif;\">" +
			"  <tbody>" +
			"<TABLE_ROWS>" +
			"  </tbody>" +
			"</table><br>" +
			"Best regards,<br>" +
			"The EPM Team<br>" +
			"<strong>Contact Us:</strong><br>" +
			"Email: support@emp.com<br>" +
			"Phone: (123) 456-7890<br>" +
			"Website: <a href='https://arun-developers.github.io/' style='text-decoration: none; color: #DC3545;'>Visit Our Website</a><br><br>" +
			"We value your continued collaboration and look forward to supporting your success!" +
			"<p style='color: #DC3545;'>Note: This email is for developer testing purposes only!</p></p>";

	public static String prepareMessage(String messageType, Map<String, Object> messageDetails) {
		String messageTemplate;
		switch (messageType) {
			case "WELCOME":
				messageTemplate = EMPLOYEE_WELCOME_MESSAGE;
				break;
			case "UPDATE":
				messageTemplate = EMPLOYEE_UPDATE_MESSAGE;
				break;
			case "CHANGE_PASSWORD":
				messageTemplate = CHANGE_USER_PASSWORD_MESSAGE;
				break;
			default:
				throw new IllegalArgumentException("Invalid message type: " + messageType);
		}

		String employeeName = (String) messageDetails.get("name");
		String employeeId = (String) messageDetails.get("employeeId");
		String employeePosition = (String) messageDetails.get("position");
		String employeeGender = (String) messageDetails.get("gender");
		String employeeLocation = (String) messageDetails.get("location");
		String employeeExperience = (String) messageDetails.get("experience");
		String userId = (String) messageDetails.get("userId");
		String role = (String) messageDetails.get("role");
		String contact = (String) messageDetails.get("contact");

		Map<String, Object> tableData = new HashMap<>();

		tableData.put("Name", employeeName);
		tableData.put("Employee ID", employeeId);
		tableData.put("Position", employeePosition);
		tableData.put("Gender", employeeGender);
		tableData.put("Location", employeeLocation);
		tableData.put("Experience", employeeExperience);
		tableData.put("User ID", userId);
		tableData.put("Role", role);
		tableData.put("Contact", contact);

		String tableRows = Table.createTableRows(tableData);

		return messageTemplate
				.replace("<EMPLOYEEID>", employeeId != null ? employeeId : "N/A")
				.replace("<EMPLOYEENAME>", employeeName != null ? employeeName : "Employee")
				.replace("<EMPLOYEEGENDER>", employeeGender != null ? employeeGender : "Gender Not Specified")
				.replace("<EMPLOYEEPOSITION>", employeePosition != null ? employeePosition : "Position Not Specified")
				.replace("<EMPLOYEELOCATION>", employeeLocation != null ? employeeLocation : "Location Not Specified")
				.replace("<EMPLOYEEEXPERIENCE>",employeeExperience != null ? employeeExperience : "Experience Not Specified")
				.replace("<TABLE_ROWS>", tableRows != null ? tableRows : "Table Not Specified");
	}

}
