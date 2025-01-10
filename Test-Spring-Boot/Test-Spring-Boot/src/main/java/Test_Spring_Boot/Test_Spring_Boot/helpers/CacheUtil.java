package Test_Spring_Boot.Test_Spring_Boot.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.Cache;

public class CacheUtil {

	public static <T> T getCachedUserDetails(Cache cache, String cacheKey, Class<T> clazz) {
		if (cache == null) {
			System.out.println("Cache not found.");
			return null;
		}

		String cachedJson = cache.get(cacheKey, String.class);
		if (cachedJson == null) {
			System.out.println("No cached data found for key: " + cacheKey);
			return null;
		}

		System.out.println("Cached Data JSON: " + cachedJson);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(cachedJson, clazz);
		} catch (Exception e) {
			System.err.println("Error parsing cached data: " + e.getMessage());
			return null;
		}
	}
}
