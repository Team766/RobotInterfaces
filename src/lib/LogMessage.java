package lib;

import java.util.Date;

public class LogMessage implements Message {
	
	public enum Level{
		INFO, DEBUG, WARNING, ERROR, FATAL
	};
	
	private String message;
	
	/**
	 * Message to be saved to the log file
	 * 
	 * @param levl Level of the message
	 * @param mess Message to be sent with the location at the front
	 */
	public LogMessage(Level levl, String mess){
		message = levl + " " + getTime() + " " + mess;
	}
	
	public LogMessage(String mess){
		this(Level.INFO, mess);
	}
	
	public String getMessage(){
		return message;
	}
	
	//Unicode time
	private String getTime(){
		return "" + System.currentTimeMillis();
	}
}
