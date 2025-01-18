package Test_Spring_Boot.Test_Spring_Boot.helpers;

public class TypeConversion {

	public static String toString(Object value) {
		if (value instanceof Integer) {
			return String.valueOf(value);
		} else if (value instanceof String) {
			return (String) value;
		}
		return null;
	}

	public static Integer toNumber(Object value) {
		if (value instanceof String) {
			try {
				return Integer.valueOf((String) value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid string value for conversion to Integer: " + value, e);
			}
		} else if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		return null;
	}
}
