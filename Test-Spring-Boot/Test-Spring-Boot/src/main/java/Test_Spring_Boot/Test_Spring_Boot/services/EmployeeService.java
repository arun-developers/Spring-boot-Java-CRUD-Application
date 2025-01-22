package Test_Spring_Boot.Test_Spring_Boot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import Test_Spring_Boot.Test_Spring_Boot.dto.UserDetailsDTO;
import Test_Spring_Boot.Test_Spring_Boot.entities.Employee;
import Test_Spring_Boot.Test_Spring_Boot.entities.UserRegister;
import Test_Spring_Boot.Test_Spring_Boot.helpers.CacheUtil;
import Test_Spring_Boot.Test_Spring_Boot.repositories.EmployeeRepository;
import Test_Spring_Boot.Test_Spring_Boot.repositories.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.cache.CacheManager;

@Service
public class EmployeeService {
	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private UserRepository userRepository;

	public Employee saveEmployee(Employee emp) {

		// Retrieve logged-in user details from cache
		Cache loggedInUsersCache = cacheManager.getCache("loggedInUsers");
		UserDetailsDTO loggedInUserDetails = CacheUtil.getCachedUserDetails(loggedInUsersCache, "loggedInUser",
				UserDetailsDTO.class);

		Long loggedInUserId = loggedInUserDetails.getId();
		UserRegister user = userRepository.findById(loggedInUserId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + loggedInUserId));

		emp.setCreatedByUser(user.getName());
		emp.setCreatedByUserId(user.getUserId());
		return employeeRepository.save(emp);
	}

	public Page<Employee> getAllEmployees(Pageable pageable) {
		return employeeRepository.findAll(pageable);
	}

	public Employee getEmployeeById(Long id) {
		return employeeRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Employee with ID " + id + " not found"));
	}

	public Page<Employee> getEmployeeByCreatedByUserId(String createdByUserId, Pageable pageable) {
		return employeeRepository.getEmployeeByCreatedByUserId(createdByUserId, pageable);
	}

	public Employee updateEmployeeById(Long id, Employee updatedEmployee) {
		Employee existingEmployee = employeeRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Employee with ID " + id + " not found"));
		existingEmployee.setAge(updatedEmployee.getAge());
		existingEmployee.setName(updatedEmployee.getName());
		existingEmployee.setEmail(updatedEmployee.getEmail());
		existingEmployee.setGender(updatedEmployee.getGender());
		existingEmployee.setPosition(updatedEmployee.getPosition());
		existingEmployee.setLocation(updatedEmployee.getLocation());
		existingEmployee.setExperience(updatedEmployee.getExperience());
		return employeeRepository.save(existingEmployee);
	}

	public void deleteEmployeeById(Long id) {
		if (!employeeRepository.existsById(id)) {
			throw new RuntimeException("Employee with ID " + id + " not found");
		}
		employeeRepository.deleteById(id);
	}
}
