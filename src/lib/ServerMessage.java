package lib;

public class ServerMessage implements Message {
	private String[] values;
	private String messageName;
	
	public ServerMessage(String msg, String[] vals) {
		messageName = msg;
		values = vals;
	}
	
	public String getMessageName() {
		return messageName;
	}
	
	public String[] getValues() {
		return values;
	}
}
