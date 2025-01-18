package Test_Spring_Boot.Test_Spring_Boot.services;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendSimpleEmail(String to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		try {
			message.setTo(to);
			message.setSubject(subject);
			message.setText(body);
			message.setFrom("aruns3435@gmail.com");
			mailSender.send(message);
			System.out.println("Email send successfully!");
		} catch (Exception e) {
			throw new RuntimeException("An error occurred while processing the email", e);
		}
	}

	public void sendHtmlEmail(String to, String subject, String htmlContent) throws UnsupportedEncodingException {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlContent, true);
			helper.setFrom("aruns3435@gmail.com", "Arun Singh");
			mailSender.send(message);
			System.out.println("HTML email sent successfully!");
		} catch (MessagingException e) {
			throw new RuntimeException("An error occurred while processing the HTML email", e);
		}
	}
}
