package Test_Spring_Boot.Test_Spring_Boot.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Test_Spring_Boot.Test_Spring_Boot.entities.Queue;
import Test_Spring_Boot.Test_Spring_Boot.helpers.Response;
import Test_Spring_Boot.Test_Spring_Boot.services.QueueService;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

	@Autowired
	private QueueService queueService;

	@GetMapping("/all")
	public ResponseEntity<Response> findAllQueues() {
		try {
			List<Queue> queueList = queueService.findAllQueues();
			// Return a success response
			return ResponseEntity.ok(Response.success(HttpStatus.OK.value(), queueList,
					"All queues retrieved successfully!", 0));

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error(HttpStatus.BAD_REQUEST.value(),
					"Error occurred while retrieving employee: " + e.getMessage()));
		}
	}
}
