package Test_Spring_Boot.Test_Spring_Boot.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Response {
	int code;
	boolean status;
	Object data;
	String message;
	int totalPages;

	public static Response success(int code, Object data, String message, int totalPages) {
		return new Response(code, true, data, message, totalPages);
	}

	public static Response error(int code, String message) {
		return new Response(code, false, null, message, 0);
	}
}
