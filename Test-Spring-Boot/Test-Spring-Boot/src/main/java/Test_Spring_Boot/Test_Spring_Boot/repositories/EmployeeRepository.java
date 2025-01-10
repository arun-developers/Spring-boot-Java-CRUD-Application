package Test_Spring_Boot.Test_Spring_Boot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import Test_Spring_Boot.Test_Spring_Boot.entities.Employee;

import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
