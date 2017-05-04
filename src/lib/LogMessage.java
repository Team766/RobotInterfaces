package lib;

public class LogMessage implements Message {
	
	private String message;
	
	public LogMessage(String letter){
		message = letter;
	}
	
	public String getMessage(){
		return message;
	}
}
