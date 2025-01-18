package Test_Spring_Boot.Test_Spring_Boot.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import Test_Spring_Boot.Test_Spring_Boot.entities.UserRegister;
import Test_Spring_Boot.Test_Spring_Boot.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	// Method to get all users
	public List<UserRegister> findAllUsers() {
		return userRepository.findAll();
	}

	// Method to find a user by email
	public Optional<UserRegister> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	// Method to find a user by contact number
	public Optional<UserRegister> findByContact(String contact) {
		return userRepository.findByContact(contact);
	}

	// Method to find users by name
	public List<UserRegister> findByName(String name) {
		return userRepository.findByName(name);
	}

	// Method to save a new user
	public UserRegister saveUser(UserRegister user) {
		return userRepository.save(user);
	}

	// Method to find a user by ID
	public UserRegister findById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User with ID " + id + " not found"));
	}

	// Method to update an existing user
	public UserRegister updateUserById(Long id, UserRegister updatedUser) {
		UserRegister existingUser = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User with ID " + id + " not found"));
		existingUser.setEmail(updatedUser.getEmail());
		existingUser.setContact(updatedUser.getContact());
		existingUser.setName(updatedUser.getName());
		existingUser.setRole(updatedUser.getRole());
		existingUser.setPassword(updatedUser.getPassword());
		existingUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		return userRepository.save(existingUser);
	}

	// Method to delete a user by ID
	public void deleteUserById(Long id) {
		if (!userRepository.existsById(id)) {
			throw new RuntimeException("User with ID " + id + " not found");
		}
		userRepository.deleteById(id);
	}

	public UserRegister updateUserPasswordById(Long id, String newPassword) {
		UserRegister existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User with ID " + id + " not found"));
		existingUser.setPassword(newPassword);
		existingUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		return userRepository.save(existingUser);
	}
}
