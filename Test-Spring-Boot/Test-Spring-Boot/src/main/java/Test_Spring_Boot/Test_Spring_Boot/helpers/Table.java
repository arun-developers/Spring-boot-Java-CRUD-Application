package Test_Spring_Boot.Test_Spring_Boot.helpers;

import java.util.Map;

public class Table {

	public static String createTableRows(Map<String, Object> data) throws NullPointerException {
		StringBuilder tableRows = new StringBuilder();
		data.forEach((key, value) -> {
			if (value != null) {
				tableRows.append("<tr>")
						.append("<td style=\"border: 1px solid #ddd; padding: 8px;\">").append(key).append("</td>")
						.append("<td style=\"border: 1px solid #ddd; padding: 8px;\">").append(value).append("</td>")
						.append("</tr>");
			}
		});

		return tableRows.toString();
	}
}
