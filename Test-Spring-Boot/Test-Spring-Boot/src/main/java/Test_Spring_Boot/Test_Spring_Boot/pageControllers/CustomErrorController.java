package Test_Spring_Boot.Test_Spring_Boot.pageControllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/error")
public class CustomErrorController {

	@GetMapping
	public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
		int status = response.getStatus();
		model.addAttribute("status", status);
		return "error";
	}
}
