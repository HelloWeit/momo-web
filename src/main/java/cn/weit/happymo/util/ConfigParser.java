package cn.weit.happymo.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author weitong
 */
public class ConfigParser {

	public Properties get(String configName) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configName);
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return properties;
	}
}
