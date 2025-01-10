package Test_Spring_Boot.Test_Spring_Boot.pageControllers;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Test_Spring_Boot.Test_Spring_Boot.dto.UserDetailsDTO;
import Test_Spring_Boot.Test_Spring_Boot.entities.Employee;
import Test_Spring_Boot.Test_Spring_Boot.helpers.CacheUtil;
import Test_Spring_Boot.Test_Spring_Boot.services.EmployeeService;
import jakarta.validation.Valid;

@Controller
public class EmployeePageController {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private CacheManager cacheManager;

	@GetMapping("/addEmployee")
	public String createEmployee(Model model) {
		Employee employee = new Employee();

		Cache loggedInUsersCache = cacheManager.getCache("loggedInUsers");
		UserDetailsDTO loggedInUserDetails = CacheUtil.getCachedUserDetails(loggedInUsersCache, "loggedInUser",
				UserDetailsDTO.class);
		if (loggedInUserDetails != null) {
			model.addAttribute("userDetails", loggedInUserDetails);
		}

		model.addAttribute("employee", employee);
		return "addEmployee";
	}

	@PostMapping("/addEmployee")
	public String createNewEmployee(@ModelAttribute @Valid Employee employee, RedirectAttributes redirectAttributes) {
		employee.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		employee.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		employeeService.saveEmployee(employee);
		redirectAttributes.addFlashAttribute("successMessage", "New employee has been created successfully!");
		return "redirect:/employees";
	}

	@GetMapping("/employees")
	public String getAllEmployeesData(Model model,
			@RequestParam(defaultValue = "0", required = false) int page,
			@RequestParam(defaultValue = "10", required = false) int size,
			@RequestParam(defaultValue = "id", required = false) String sortBy,
			@RequestParam(defaultValue = "true", required = false) boolean ascending) {

		// Configure sorting and pagination
		Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sort);

		// Retrieve logged-in user details from cache
		Cache loggedInUsersCache = cacheManager.getCache("loggedInUsers");
		UserDetailsDTO loggedInUserDetails = CacheUtil.getCachedUserDetails(loggedInUsersCache, "loggedInUser",
				UserDetailsDTO.class);

		// Fetch paginated employee data
		Page<Employee> pageableEmployeesList = employeeService.getAllEmployees(pageable);
		List<Employee> employeeList = pageableEmployeesList.getContent();

		try {
			if (!employeeList.isEmpty()) {
				model.addAttribute("employees", employeeList);
			} else {
				model.addAttribute("dataNotFound", "Opps! The requested data could not be found!");
			}
			if (loggedInUserDetails != null) {
				model.addAttribute("userDetails", loggedInUserDetails);
			}
		} catch (Exception e) {
			System.err.println("Error retrieving employee: " + e.getMessage());
			model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
		}
		return "employees";
	}

	@GetMapping("/updateEmployee/{id}")
	public String updateEmployee(@PathVariable Long id, Model model) {
		try {
			Employee employeeDetails = employeeService.getEmployeeById(id);
			Cache loggedInUsersCache = cacheManager.getCache("loggedInUsers");
			UserDetailsDTO loggedInUserDetails = CacheUtil.getCachedUserDetails(loggedInUsersCache, "loggedInUser",
					UserDetailsDTO.class);
			if (employeeDetails != null) {
				model.addAttribute("employee", employeeDetails);
			} else {
				model.addAttribute("errorMessage",
						"Oops! The employee ID provided is either invalid or does not exist!");
			}

			if (loggedInUserDetails != null) {
				model.addAttribute("userDetails", loggedInUserDetails);
			}
		} catch (Exception e) {
			System.err.println("Error retrieving employee: " + e.getMessage());
			model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
		}
		return "updateEmployee";
	}

	@PostMapping("/updateEmployee/{id}")
	public String updateEmployeeById(@PathVariable Long id, RedirectAttributes redirectAttributes, Employee employee) {
		try {
			Employee updatedEmployeeDetails = employeeService.updateEmployeeById(id, employee);
			if (updatedEmployeeDetails != null) {
				redirectAttributes.addFlashAttribute("successMessage",
						"Employee with ID:" + id + "has been updated successfully!");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage",
						"The employee ID provided is either invalid or does not exist!");
			}
		} catch (Exception e) {
			System.err.println("Error retrieving employee: " + e.getMessage());
			redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred. Please try again.");
		}
		return "redirect:/employees";
	}

	@GetMapping("delete/emp/{id}")
	public String deleteEmpData(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			employeeService.deleteEmployeeById(id);
			redirectAttributes.addFlashAttribute("successMessage",
					"Record with ID: " + id + " has been successfully deleted");
		} catch (Exception e) {
			System.err.println("Error retrieving employee: " + e.getMessage());
			redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred. Please try again.");
		}
		return "redirect:/employees";
	}
}
