package Test_Spring_Boot.Test_Spring_Boot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import Test_Spring_Boot.Test_Spring_Boot.entities.Employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	@Query("SELECT e FROM Employee e WHERE e.createdByUserId = :createdByUserId")
	Page<Employee> getEmployeeByCreatedByUserId(String createdByUserId, Pageable pageable);
}
