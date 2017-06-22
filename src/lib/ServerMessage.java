package lib;

public class ServerMessage implements Message{
	String[] values;
	String messageName;
	
	public ServerMessage(String msg, String[] vals){
		messageName = msg;
		values = vals;
	}
	
	public String getMessageName(){
		return messageName;
	}
	
	public String[] getValues(){
		return values;
	}
}
