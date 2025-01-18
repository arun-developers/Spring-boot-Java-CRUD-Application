package Test_Spring_Boot.Test_Spring_Boot.helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IDGenerator {

	public static String generateStringId(String initialConstant, int employeeNumber) {
		LocalDate now = LocalDate.now();
		String yearMonth = now.format(DateTimeFormatter.ofPattern("yyMM"));
		String formattedEmployeeNumber = String.format("%04d", employeeNumber);
		return initialConstant + "-" + yearMonth + "-" + formattedEmployeeNumber;
	}
}
