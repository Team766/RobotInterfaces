package lib;

import java.util.HashMap;

public class LogFactory {
	private static HashMap<String, Logger> logs = new HashMap<String, Logger>();
	
	public synchronized static Logger getInstance(String key){
		return logs.get(key);
	}
	
	/**
	 * Only call in CommandBase init()
	 * @param key The name of the new log
	 */
	public synchronized static void createInstance(String key){
		Logger adding = new Logger(key);
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
