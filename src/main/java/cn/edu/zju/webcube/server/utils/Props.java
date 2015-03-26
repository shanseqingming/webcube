package cn.edu.zju.webcube.server.utils;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class Props {

	private HashMap<String, String> _current = new HashMap<String, String>();
	private static Props instance = null;
	private static final Logger log = Logger.getLogger(Props.class);
	public static String CUBE_META_FILE_LOCATION = "";

	public static Props getInstance(){
		if(instance == null){
			log.error("Null props instance");
			throw new NullPointerException("No props instance");
		}
		return instance;
	}

	public static void initInstance(String fileLocation) {
		instance = new Props(fileLocation);
	}


	private Props(String fileStr) {
		try {
			Properties p = getProperties(fileStr);
			this.put(p);

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void put(Properties properties) {
		for (String propName : properties.stringPropertyNames()) {
			this._current.put(propName, properties.getProperty(propName));
		}
	}

	public HashMap<String, String> get_current() {
		return _current;
	}

	public static Properties getProperties(String fileLocation) throws IOException {
		FileInputStream input = new FileInputStream(fileLocation);
		Properties properties = new Properties();
		properties.load(input);
		input.close();
		return properties;
	}
}
