package lib;

import java.util.Date;

public class LogMessage implements Message {
	
	public enum Level{
		INFO, DEBUG, WARNING, ERROR, FATAl
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
	
	private String getTime(){
		//Time in GMT so subtract 7 hours to get to Pacific
		return new Date().toString().substring(12, 20);
	}
}
