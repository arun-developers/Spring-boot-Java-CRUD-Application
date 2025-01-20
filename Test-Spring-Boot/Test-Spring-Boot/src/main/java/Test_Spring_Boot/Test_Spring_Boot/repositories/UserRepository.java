package Test_Spring_Boot.Test_Spring_Boot.repositories;

import Test_Spring_Boot.Test_Spring_Boot.entities.UserRegister;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserRegister, Long> {
	Optional<UserRegister> findByEmail(String email);

	Optional<UserRegister> findByContact(String contact);

	List<UserRegister> findByName(String name);

	Optional<UserRegister> findByUserId(String userId);
}