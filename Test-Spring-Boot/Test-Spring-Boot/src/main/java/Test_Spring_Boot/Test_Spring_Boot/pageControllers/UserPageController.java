package Test_Spring_Boot.Test_Spring_Boot.pageControllers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Test_Spring_Boot.Test_Spring_Boot.dto.ChangePasswordDTO;
import Test_Spring_Boot.Test_Spring_Boot.dto.LoginRequest;
import Test_Spring_Boot.Test_Spring_Boot.dto.ForgotPasswordDTO;
import Test_Spring_Boot.Test_Spring_Boot.entities.UserRegister;
import Test_Spring_Boot.Test_Spring_Boot.helpers.AvatarCreator;
import Test_Spring_Boot.Test_Spring_Boot.helpers.JwtUtil;
import Test_Spring_Boot.Test_Spring_Boot.helpers.PasswordValidator;
import Test_Spring_Boot.Test_Spring_Boot.services.CustomUserDetails;
import Test_Spring_Boot.Test_Spring_Boot.services.CustomUserDetailsService;
import Test_Spring_Boot.Test_Spring_Boot.services.QueueService;
import Test_Spring_Boot.Test_Spring_Boot.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class UserPageController {

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

	@Autowired
	private QueueService queueService;

	@GetMapping("/")
	public String welcome(Model model) {
		System.out.println("Accessed welcome page");
		return "home";
	}

	@GetMapping("/signup")
	public String userSignup(Model model) {
		UserRegister userRegister = new UserRegister();
		model.addAttribute("userRegister", userRegister);
		return "registration";
	}

	@PostMapping("/signup")
	public String handleSignup(
			@ModelAttribute @Valid UserRegister userRegister,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			return "registration";
		}

		Optional<UserRegister> existingUser = userService.findByEmail(userRegister.getEmail());
		if (existingUser.isPresent()) {
			redirectAttributes.addFlashAttribute("errorMessage", "An account with this email already exists.");
			return "redirect:/signup";
		}

		boolean passwordStrength = PasswordValidator.StrongPasswordValidator(userRegister.getPassword());
		if (!passwordStrength) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Weak password. Minimum 8 characters, at least one uppercase, one lowercase, one digit, and one special character.");
			return "redirect:/signup";
		}

		try {
			String hashedPassword = passwordEncoder.encode(userRegister.getPassword());
			userRegister.setPassword(hashedPassword);
			userRegister.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			userRegister.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			userService.saveUser(userRegister);
			redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
			return "redirect:/login";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Failed to register user: " + e.getMessage());
			return "redirect:/signup";
		}
	}

	@GetMapping("/login")
	public String userLogin(Model model) {
		System.out.println("Accessed login page");
		LoginRequest loginRequest = new LoginRequest();
		model.addAttribute("loginRequest", loginRequest);
		return "login";
	}

	@PostMapping("/login")
	public String handleLogin(@Valid @ModelAttribute LoginRequest loginRequest, BindingResult result,
			RedirectAttributes redirectAttributes, HttpServletResponse response) throws JsonProcessingException {
		if (result.hasErrors()) {
			return "login";

		}
		String email = loginRequest.getEmail();
		String password = loginRequest.getPassword();
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
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
		responseData.put("userDetails", customUserDetails);

		// Store token in a cookie
		Cookie jwtCookie = new Cookie("token", token);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(60 * 60 * 24); // 1 day
		response.addCookie(jwtCookie);

		Cache loggedInUsersCache = cacheManager.getCache("loggedInUsers");
		// Store user details in cache
		if (loggedInUsersCache != null) {
			loggedInUsersCache.put("loggedInUser", userDetailsJson);
		}

		redirectAttributes.addFlashAttribute("successMessage", "User login successfully!");
		return "redirect:/employees";
	}

	@GetMapping("/changePassword")
	public String changePassword(Model model) {
		System.out.println("Accessed forgotPassword page");
		return "changePassword";
	}

	@PostMapping("/changePassword")
	public String changePassword(@Valid @ModelAttribute ChangePasswordDTO changePasswordDTO, BindingResult result,
			RedirectAttributes redirectAttributes) {

		String email = changePasswordDTO.getEmail();
		String previousPassword = changePasswordDTO.getPreviousPassword();
		String newPassword = changePasswordDTO.getNewPassword();
		String confirmNewPassword = changePasswordDTO.getConfirmNewPassword();

		if (result.hasErrors()) {
			return "changePassword";
		}

		Optional<UserRegister> existingUser = userService.findByEmail(email);
		if (!existingUser.isPresent()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Oops: No account exists with this email address!");
			return "redirect:/signup";
		}

		if (!passwordEncoder.matches(previousPassword, existingUser.get().getPassword())) {
			redirectAttributes.addFlashAttribute("errorMessage", "Previous password is incorrect!");
			return "redirect:/changePassword";
		}

		if (!newPassword.equals(confirmNewPassword)) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Oops: New password and confirm new password do not match!");
			return "redirect:/changePassword";
		}

		if (passwordEncoder.matches(previousPassword, passwordEncoder.encode(confirmNewPassword))) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Oops: The new password must be different from the previous password!");
			return "redirect:/changePassword";
		}

		boolean confirmPasswordStrength = PasswordValidator.StrongPasswordValidator(confirmNewPassword);
		if (!confirmPasswordStrength) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Weak password. Minimum 8 characters, at least one uppercase, one lowercase, one digit, and one special character!");
			return "redirect:/changePassword";
		}

		try {

			String hashedConfirmPassword = passwordEncoder.encode(confirmNewPassword);
			UserRegister updatedUserDetails = userService.updateUserPasswordById(existingUser.get().getId(),
					hashedConfirmPassword);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> payloadMap = new HashMap<>();

			payloadMap.put("id", updatedUserDetails.getId());
			payloadMap.put("name", updatedUserDetails.getName());
			payloadMap.put("emailId", updatedUserDetails.getEmail());
			payloadMap.put("userId", updatedUserDetails.getUserId());
			payloadMap.put("role", updatedUserDetails.getRole());
			payloadMap.put("contact", updatedUserDetails.getContact());

			// Convert the map to a JSON string
			String payload = objectMapper.writeValueAsString(payloadMap);
			queueService.addTask("changePasswordNotification", payload);

			redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
			return "redirect:/login";

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password: " + e.getMessage());
			return "redirect:/changePassword";
		}
	}

	@GetMapping("/generateResetPasswordLink")
	public String generateResetPasswordLink(Model model) {
		System.out.println("Accessed generate forgot password link page");
		return "generateResetPasswordLink";
	}

	@PostMapping("/generateResetPasswordLink")
	public String generateResetPasswordLink(@ModelAttribute ForgotPasswordDTO forgotPasswordDTO,
			RedirectAttributes redirectAttributes, HttpServletRequest request) {

		String email = forgotPasswordDTO.getEmail();
		Optional<UserRegister> existingUser = userService.findByEmail(email);

		if (!existingUser.isPresent()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Oops: No account exists with this email address!");
			return "redirect:/signup";
		}

		try {

			UserRegister user = existingUser.get();
			String userId = String.valueOf(user.getUserId());
			long timestamp = Instant.now().toEpochMilli();
			String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
			String resetLink = baseUrl + "/resetPassword/" + timestamp + "/" + userId;

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> payloadMap = new HashMap<>();

			payloadMap.put("id", user.getId());
			payloadMap.put("name", user.getName());
			payloadMap.put("emailId", user.getEmail());
			payloadMap.put("userId", user.getUserId());
			payloadMap.put("url", resetLink);

			// Convert the map to a JSON string
			String payload = objectMapper.writeValueAsString(payloadMap);
			queueService.addTask("generateResetPasswordNotification", payload);

			redirectAttributes.addFlashAttribute("successMessage",
					"A password reset link has been successfully sent to your registered email address!");
			return "redirect:/login";

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Failed to generate reset password link: " + e.getMessage());
			return "redirect:/generateResetPasswordLink";
		}

	}

	@GetMapping("/resetPassword/{timestamp}/{userId}")
	public String resetPassword(Model model, @PathVariable long timestamp, @PathVariable String userId,
			RedirectAttributes redirectAttributes) {

		Optional<UserRegister> existingUser = userService.findByUserId(userId);

		if (!existingUser.isPresent()) {
			redirectAttributes.addFlashAttribute("errorMessage", "No user found with the provided ID");
			return "redirect:/signup";
		}

		long currentTimestamp = Instant.now().toEpochMilli();
		long validityPeriod = 15 * 60 * 1000; // For 15 minutes in secounds

		if (currentTimestamp - timestamp > validityPeriod) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"The reset link you used is no longer active and has expired");
			return "redirect:/errorPage";
		}

		try {
			model.addAttribute("timestamp", timestamp);
			model.addAttribute("userId", userId);
			return "resetPassword";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Failed to load reset password page: " + e.getMessage());
			return "redirect:/generateResetPasswordLink";
		}

	}

	@PostMapping("/resetPassword/{timestamp}/{userId}")
	public String resetPassword(@ModelAttribute ForgotPasswordDTO forgotPasswordDTO,
			@PathVariable long timestamp,
			@PathVariable String userId,
			RedirectAttributes redirectAttributes) {

		String newPassword = forgotPasswordDTO.getNewPassword();
		String confirmNewPassword = forgotPasswordDTO.getConfirmNewPassword();
		Optional<UserRegister> existingUser = userService.findByUserId(userId);

		if (!existingUser.isPresent()) {
			redirectAttributes.addFlashAttribute("errorMessage", "No user found with the provided ID");
			return "redirect:/signup";
		}

		if (!newPassword.equals(confirmNewPassword)) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Oops: New password and confirm new password do not match!");
			return "redirect:/resetPassword/" + timestamp + "/" + userId;
		}

		boolean confirmPasswordStrength = PasswordValidator.StrongPasswordValidator(confirmNewPassword);
		if (!confirmPasswordStrength) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Weak password. Minimum 8 characters, at least one uppercase, one lowercase, one digit, and one special character!");
			return "redirect:/resetPassword/" + timestamp + "/" + userId;
		}

		long currentTimestamp = Instant.now().toEpochMilli();
		long validityPeriod = 15 * 60 * 1000; // For 15 minutes in secounds

		if (currentTimestamp - timestamp > validityPeriod) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"The reset link you used is no longer active and has expired");
			return "redirect:/errorPage";
		}

		try {

			String hashedConfirmPassword = passwordEncoder.encode(confirmNewPassword);
			UserRegister updatedUserDetails = userService.updateUserPasswordById(existingUser.get().getId(),
					hashedConfirmPassword);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> payloadMap = new HashMap<>();

			payloadMap.put("id", updatedUserDetails.getId());
			payloadMap.put("name", updatedUserDetails.getName());
			payloadMap.put("emailId", updatedUserDetails.getEmail());
			payloadMap.put("userId", updatedUserDetails.getUserId());
			payloadMap.put("role", updatedUserDetails.getRole());
			payloadMap.put("contact", updatedUserDetails.getContact());

			// Convert the map to a JSON string
			String payload = objectMapper.writeValueAsString(payloadMap);
			queueService.addTask("resetPasswordNotification", payload);

			redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully!");
			return "redirect:/login";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Failed to reset password: " + e.getMessage());
			return "redirect:/resetPassword/" + timestamp + "/" + userId;
		}
	}

}
