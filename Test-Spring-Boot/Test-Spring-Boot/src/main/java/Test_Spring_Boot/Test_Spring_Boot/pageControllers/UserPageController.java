package Test_Spring_Boot.Test_Spring_Boot.pageControllers;

import java.sql.Timestamp;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Test_Spring_Boot.Test_Spring_Boot.dto.LoginRequest;
import Test_Spring_Boot.Test_Spring_Boot.entities.UserRegister;
import Test_Spring_Boot.Test_Spring_Boot.helpers.AvatarCreator;
import Test_Spring_Boot.Test_Spring_Boot.helpers.JwtUtil;
import Test_Spring_Boot.Test_Spring_Boot.helpers.PasswordValidator;
import Test_Spring_Boot.Test_Spring_Boot.services.CustomUserDetails;
import Test_Spring_Boot.Test_Spring_Boot.services.CustomUserDetailsService;
import Test_Spring_Boot.Test_Spring_Boot.services.UserService;
import jakarta.servlet.http.Cookie;
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

	@GetMapping("/forgotPassword")
	public String forgotPassword(Model model) {
		System.out.println("Accessed forgotPassword page");
		return "forgotPassword";
	}

	@GetMapping("/")
	public String welcome(Model model) {
		System.out.println("Accessed welcome page");
		return "home";
	}
}
