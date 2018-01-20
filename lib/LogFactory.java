package lib;

import java.util.HashMap;

public class LogFactory {
	private static HashMap<String, Logger> logs = new HashMap<String, Logger>();
	
	public synchronized static Logger getInstance(String key){
		Logger instance = logs.get(key);
		if(instance == null){
			instance = new Logger(key);
			logs.put(key, instance);
		}
		return instance;
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
