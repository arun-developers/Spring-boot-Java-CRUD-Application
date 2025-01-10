package Test_Spring_Boot.Test_Spring_Boot.helpers;

public class AvatarCreator {

	public static String getInitials(String name) {
		// Split the name into words
		String[] words = name.trim().split("\\s+");
		StringBuilder initialsBuilder = new StringBuilder();

		// Extract the first letter of each word
		for (String word : words) {
			if (!word.isEmpty()) {
				initialsBuilder.append(word.charAt(0));
			}
		}

		// Convert to uppercase for a clean appearance
		return initialsBuilder.toString().toUpperCase();
	}
}
