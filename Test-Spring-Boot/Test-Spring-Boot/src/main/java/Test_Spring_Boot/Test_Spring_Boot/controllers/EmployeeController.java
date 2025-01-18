package Test_Spring_Boot.Test_Spring_Boot.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.DeleteMapping;

import Test_Spring_Boot.Test_Spring_Boot.dto.EncryptedDataDTO;
import Test_Spring_Boot.Test_Spring_Boot.entities.Employee;
import Test_Spring_Boot.Test_Spring_Boot.helpers.Response;
import Test_Spring_Boot.Test_Spring_Boot.helpers.ValidationMessage;
import Test_Spring_Boot.Test_Spring_Boot.services.DataService;
import Test_Spring_Boot.Test_Spring_Boot.services.EmployeeService;
import Test_Spring_Boot.Test_Spring_Boot.services.QueueService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private DataService dataService;

	@Autowired
	private QueueService queueService;

	// POST METHOD TO ADD EMP
	@PostMapping("/employee/add")
	public ResponseEntity<Response> addEmployee(@Valid @RequestBody Employee emp, BindingResult result) {

		if (result.hasErrors()) {
			String errors = ValidationMessage.formatValidationErrors(result);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error(HttpStatus.BAD_REQUEST.value(), errors));
		}
		try {
			emp.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			emp.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

			// Encrypt the employee object fields after validation
			String encryptedDataString = dataService.encryptObject(emp);

			// Save the employee
			Employee savedEmployee = employeeService.saveEmployee(emp);
			System.out.println("Saved Employee Details successfully!: " + savedEmployee);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> payloadMap = new HashMap<>();

			payloadMap.put("id", savedEmployee.getId());
			payloadMap.put("name", savedEmployee.getName());
			payloadMap.put("employeeId", savedEmployee.getEmployeeId());
			payloadMap.put("position", savedEmployee.getPosition());
			payloadMap.put("location", savedEmployee.getLocation());
			payloadMap.put("emailId", savedEmployee.getEmail());
			payloadMap.put("gender", savedEmployee.getGender());
			payloadMap.put("experience", savedEmployee.getExperience());

			// Convert the map to a JSON string
			String payload = objectMapper.writeValueAsString(payloadMap);
			queueService.addTask("employeeOnboardingNotification", payload);

			// Create a DTO for the response
			EncryptedDataDTO encryptedDataResponse = new EncryptedDataDTO(encryptedDataString);

			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Response.success(HttpStatus.CREATED.value(), encryptedDataResponse,
							"Employee added successfully!", 0));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Response.error(500, "Failed to add employee: " + e.getMessage()));
		}
	}

	@GetMapping(path = "/employees")
	public ResponseEntity<Response> getEmployeesData(
			@RequestParam(defaultValue = "0", required = false) int page,
			@RequestParam(defaultValue = "10", required = false) int size,
			@RequestParam(defaultValue = "false", required = false) boolean isEncrypted,
			@RequestParam(defaultValue = "id", required = false) String sortBy,
			@RequestParam(defaultValue = "true", required = false) boolean ascending) {

		try {
			// Configure sorting and pagination
			Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
			Pageable pageable = PageRequest.of(page, size, sort);

			// Fetch paginated employee data
			Page<Employee> pageableEmployeesList = employeeService.getAllEmployees(pageable);
			List<Employee> employeeList = pageableEmployeesList.getContent();

			// Total pages available
			int totalPages = pageableEmployeesList.getTotalPages();

			// Encrypt data if required
			Object responseData = isEncrypted ? dataService.encryptObject(employeeList) : employeeList;

			// Return a success response
			return ResponseEntity.ok(Response.success(HttpStatus.OK.value(), responseData,
					(isEncrypted ? "Encrypted " : "Employees ") + "data retrieved successfully!", totalPages));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error(HttpStatus.BAD_REQUEST.value(),
					"Error occurred while retrieving employee: " + e.getMessage()));
		}
	}

	@PutMapping("employee/update/{id}")
	public ResponseEntity<Response> updateEmployeeData(
			@PathVariable Long id,
			@Valid @RequestBody Employee updatedEmployee, BindingResult result) {

		if (result.hasErrors()) {
			String errors = ValidationMessage.formatValidationErrors(result);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error(HttpStatus.BAD_REQUEST.value(), errors));
		}

		try {
			Employee employee = employeeService.updateEmployeeById(id, updatedEmployee);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> payloadMap = new HashMap<>();
			payloadMap.put("id", employee.getId());
			payloadMap.put("name", employee.getName());
			payloadMap.put("emailId", employee.getEmail());
			payloadMap.put("employeeId", employee.getEmployeeId());
			payloadMap.put("position", employee.getPosition());
			payloadMap.put("location", employee.getLocation());
			payloadMap.put("gender", employee.getGender());
			payloadMap.put("experience", employee.getExperience());

			// Convert the map to a JSON string
			String payload = objectMapper.writeValueAsString(payloadMap);
			queueService.addTask("employeeUpdateNotification", payload);

			return ResponseEntity
					.ok(Response.success(HttpStatus.OK.value(), employee,
							"Record with ID: " + id + " has been successfully updated", 0));
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error(HttpStatus.BAD_REQUEST.value(),
							"Error occurred while updating employee!: " + e.getMessage()));
		}
	}

	@DeleteMapping("employee/delete/{id}")
	public ResponseEntity<Response> deleteEmployeeData(
			@PathVariable(required = true) Long id,
			@RequestParam(required = false) boolean isEncrypted) {
		try {
			Object deletedEmployee = isEncrypted ? dataService.encryptObject(employeeService.getEmployeeById(id))
					: employeeService.getEmployeeById(id);
			employeeService.deleteEmployeeById(id);

			return ResponseEntity.ok(Response.success(HttpStatus.OK.value(), deletedEmployee,
					(isEncrypted ? "Encrypted " : "") + "Record with ID: " + id + " has been successfully deleted", 0));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error(HttpStatus.BAD_REQUEST.value(),
							"Error occurred while deleting employee!: " + e.getMessage()));
		}
	}
}
