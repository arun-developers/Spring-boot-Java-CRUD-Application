package Test_Spring_Boot.Test_Spring_Boot.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import Test_Spring_Boot.Test_Spring_Boot.helpers.AvatarCreator;
import Test_Spring_Boot.Test_Spring_Boot.helpers.JwtUtil;
import Test_Spring_Boot.Test_Spring_Boot.helpers.PasswordValidator;
import Test_Spring_Boot.Test_Spring_Boot.helpers.Response;
import Test_Spring_Boot.Test_Spring_Boot.authenticationRequest.AuthenticationRequest;
import Test_Spring_Boot.Test_Spring_Boot.entities.UserRegister;
import Test_Spring_Boot.Test_Spring_Boot.helpers.ValidationMessage;
import Test_Spring_Boot.Test_Spring_Boot.services.CustomUserDetails;
import Test_Spring_Boot.Test_Spring_Boot.services.CustomUserDetailsService;
import Test_Spring_Boot.Test_Spring_Boot.services.UserService;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private CacheManager cacheManager;

	// User signup route
	@PostMapping("/signup")
	public ResponseEntity<Response> userRegistration(@Valid @RequestBody UserRegister payload,
			BindingResult result) {
		Optional<UserRegister> existingUser = userService.findByEmail(payload.getEmail());
		boolean passwordStrength = PasswordValidator.StrongPasswordValidator(payload.getPassword());

		if (result.hasErrors()) {
			String errors = ValidationMessage.formatValidationErrors(result);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error(400, errors));
		}
		if (existingUser.isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(Response.error(409,
							"An account with this email already exists. Please use a different email"));
		}
		if (!passwordStrength) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error(400,
							"Weak password. Minimum 8 characters, at least one uppercase, one lowercase, one digit, and one special character"));
		}
		try {
			String hashedPassword = passwordEncoder.encode(payload.getPassword());
			payload.setPassword(hashedPassword);
			userService.saveUser(payload);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Response.success(201, payload, "User created successfully!", 0));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Response.error(500, "Failed to add employee: " + e.getMessage()));
		}
	}

	@PutMapping("user/update/{id}")
	public ResponseEntity<Response> updateUserById(
			@PathVariable Long id,
			@Valid @RequestBody UserRegister updatedUser,
			BindingResult result) {

		if (result.hasErrors()) {
			String errors = ValidationMessage.formatValidationErrors(result);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error(400, errors));
		}

		try {
			String hashedPassword = passwordEncoder.encode(updatedUser.getPassword());
			updatedUser.setPassword(hashedPassword);
			UserRegister user = userService.updateUserById(id, updatedUser);
			return ResponseEntity
					.ok(Response.success(200, user, "Record with ID: " + id + " has been successfully updated", 0));
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error(400, "Error occurred while updating employee!: " + e.getMessage()));
		}

	}

	@DeleteMapping("user/delete/{id}")
	public ResponseEntity<Response> deleteEmployeeData(
			@PathVariable(required = true) Long id) {
		try {
			UserRegister deletedUser = userService.findById(id);
			userService.deleteUserById(id);

			return ResponseEntity.ok(Response.success(200, deletedUser,
					"Record with ID: " + id + " has been successfully deleted", 0));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Response.error(400, "Error occurred while deleting employee!: " + e.getMessage()));
		}
	}

	@GetMapping("user/{id}")
	public ResponseEntity<Response> getUserById(@PathVariable Long id) {

		try {
			UserRegister user = userService.findById(id);
			return ResponseEntity.status(HttpStatus.OK)
					.body(Response.success(200, user,
							"Record with ID: " + id + " retrieved successfully!", 0));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Response.error(500, "Failed to fetch users: " + e.getMessage()));
		}
	}

	@GetMapping("/users")
	public ResponseEntity<Response> getAllUsers() {
		try {
			List<UserRegister> allUsers = userService.findAllUsers();
			return ResponseEntity.status(HttpStatus.OK)
					.body(Response.success(200, allUsers, "Users fetched successfully!", 0));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Response.error(500, "Failed to fetch users: " + e.getMessage()));
		}
	}

	@PostMapping("/login")
	public ResponseEntity<Response> userlogin(@RequestBody AuthenticationRequest authenticationRequest) {

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));

			UserDetails userDetails = customUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
			CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
			String token = jwtUtil.generateToken(userDetails);
			Map<String, Object> responseData = new HashMap<>();
			ObjectMapper objectMapper = new ObjectMapper();

			@SuppressWarnings("unchecked")
			Map<String, Object> userDetailsMap = objectMapper.convertValue(customUserDetails, Map.class);
			userDetailsMap.keySet().removeAll(List.of("authorities", "password"));
			String getInitials = AvatarCreator.getInitials(customUserDetails.getName());
			userDetailsMap.put("initials", getInitials);
			String userDetailsJson = objectMapper.writeValueAsString(userDetailsMap);

			responseData.put("token", token);
			responseData.put("userDetails", userDetails);

			Cache loggedInUsersCache = cacheManager.getCache("loggedInUsers");
			// Store user details in cache
			if (loggedInUsersCache != null) {
				loggedInUsersCache.put("loggedInUser", userDetailsJson);
			}

			return ResponseEntity.ok(Response.success(200, responseData, "User login successfully!", 0));
		} catch (Exception e) {
			System.out.println("Error during authentication: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Response.error(401, "Failed to login user: " + e.getMessage()));
		}
	}

}
