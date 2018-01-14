package lib;

public abstract class StatusUpdateMessage implements Message{

	protected boolean isDone = false;
	protected Message currentExecutingMessage;
	
	public StatusUpdateMessage(boolean done, Message currentMessage){
		currentExecutingMessage = currentMessage;
		isDone = done;
	}
	
	public boolean isDone(){
		return isDone;
	}	
	
	public Message getCurrentMessage(){
		return currentExecutingMessage;
	}
		
	public abstract String toString();
}
