package Test_Spring_Boot.Test_Spring_Boot.helpers;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ValidationMessage {

	public static String formatValidationErrors(BindingResult result) {
		if (result == null || !result.hasErrors()) {
			return "No validation errors.";
		}
		StringBuilder errorMessage = new StringBuilder("Validation errors: ");
		for (FieldError error : result.getFieldErrors()) {
			errorMessage.append(String.format("'%s': %s; ", error.getField(), error.getDefaultMessage()));
		}
		return errorMessage.toString();
	}

}
