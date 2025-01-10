package Test_Spring_Boot.Test_Spring_Boot.services;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import Test_Spring_Boot.Test_Spring_Boot.helpers.EncryptionDecryptionUtil;

@Service
public class DataService {

	private final ObjectMapper objectMapper = new ObjectMapper();

	public String encryptObject(Object obj) {
		try {
			String jsonData = objectMapper.writeValueAsString(obj);
			return EncryptionDecryptionUtil.encrypt(jsonData);
		} catch (Exception e) {
			throw new RuntimeException("Error encrypting object", e);
		}
	}

	public <T> T decryptObject(String encryptedData, Class<T> clazz) {
		try {
			String jsonData = EncryptionDecryptionUtil.decrypt(encryptedData);
			return objectMapper.readValue(jsonData, clazz);
		} catch (Exception e) {
			throw new RuntimeException("Error decrypting object", e);
		}
	}

	// public String encryptData(String data) {
	// return EncryptionDecryptionUtil.encrypt(data);

	// }

	// public String decryptData(String encryptedData) {
	// return EncryptionDecryptionUtil.decrypt(encryptedData);
	// }
}
