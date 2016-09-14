package lib;

import java.io.IOException;
import java.util.HashMap;

public class LogFactory {
	private static HashMap<String, Logger> logs = new HashMap<String, Logger>();
	
	public static Logger getInstance(String key){
		return logs.get(key);
	}
	
	/**
	 * Only call in CommandBase init()
	 * @param key The name of the new log
	 */
	public static void createInstance(String key){
		Logger adding;
		try {
			adding = new Logger(key);
		} catch (IOException e) {
			adding = new Logger();
		}
		logs.put(key, adding);
	}
	
	public static void closeFile(String key){
		logs.get(key).closeFile();
//		logs.remove(key);
	}
	
	public static void closeFiles(){
		for(String log : logs.keySet()){
			closeFile(log);
		}
	}
	
	public static HashMap<String, Logger> getLogs(){
		return logs;
	}
}
