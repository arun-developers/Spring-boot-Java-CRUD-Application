package Test_Spring_Boot.Test_Spring_Boot.services;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Test_Spring_Boot.Test_Spring_Boot.entities.Employee;
import Test_Spring_Boot.Test_Spring_Boot.repositories.EmployeeRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class EmployeeService {
	@Autowired
	private EmployeeRepository employeeRepository;

	public Employee saveEmployee(Employee emp) {
		return employeeRepository.save(emp);
	}

	public Page<Employee> getAllEmployees(Pageable pageable) {
		return employeeRepository.findAll(pageable);
	}

	public Employee getEmployeeById(Long id) {
		return employeeRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Employee with ID " + id + " not found"));
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
		existingEmployee.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		return employeeRepository.save(existingEmployee);
	}

	public void deleteEmployeeById(Long id) {
		if (!employeeRepository.existsById(id)) {
			throw new RuntimeException("Employee with ID " + id + " not found");
		}
		employeeRepository.deleteById(id);
	}
}
