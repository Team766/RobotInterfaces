package lib;

import interfaces.HighPriorityMessage;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class Actor implements Runnable{
	
	private final long RUN_TIME = 10;
	private final int MAX_MESSAGES = 15;
	private long lastSleepTime = 0;
	
	public double itsPerSec = 0;
	protected boolean done = false;
	
	public Class<? extends Message>[] acceptableMessages = (Class<? extends Message>[])new Class[]{};
	private LinkedBlockingQueue<Message> inbox = new LinkedBlockingQueue<Message>();
	
	public Class<? extends Actor>[] actorHierarchy = (Class<? extends Actor>[])new Class[]{};
	
	public boolean enabled = true;
	
	public abstract void init();
	
	public void filterMessages(){
	}
	
	public int countMessages(Message messages){
		int sum = 0;
		for(Message m : inbox.toArray(new Message[0])){
			if(m.getClass().equals(messages.getClass()))
				sum++;
		}
		return sum;
	}
	
	protected boolean newMessage(){
		return inbox.size() > 0;
	}
	
	public void clearInbox(){
		inbox.clear();
	}
	
	protected boolean keepMessage(Message m){
	    for(Class<? extends Message> message : acceptableMessages){
	    	if(m.getClass().equals(message)){
	            return true;
	        }
	    	//LogFactory.getInstance("General").print("Message rejected: " + message.getName());
	    	//System.out.println(toString() + " Rejected: " + m.toString());
	    }
	    return false;
	}

	public void tryAddingMessage(Message m){
		if(keepMessage(m)){	    
	        //Check for room in inbox
			if(inbox.size() >= MAX_MESSAGES){
				removeMessage();
			}
			
	   	  	try {
	            inbox.put(m);
//	            System.out.println("Adding: " + m.toString());
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	            LogFactory.getInstance("General").print("Failed to add Message:\t" + m);
	        }
	    }
	}
	
	private void removeMessage(){
		for(Message mess : inbox){
			if(!(mess instanceof HighPriorityMessage)){
				//System.out.println("Removing: " + mess.toString());
				inbox.remove(mess);
				return;
			}
		}
		LogFactory.getInstance("General").print("Failed to remove a message, all high priority");
	}
	
	public void sendMessage(Message mess){
		try {
			Scheduler.getInstance().sendMessage(mess);
		} catch (InterruptedException e) {
			System.err.println("Failed to send message: " + toString());
			LogFactory.getInstance("General").print("Actor: Failed to send message: " + toString());
			e.printStackTrace();
		}
	}
	
	public Message readMessage(){
		return inbox.poll();
//		try {
//			return inbox.take();
//		} catch (InterruptedException e) {
//			System.err.println("Failed to readMessage: " + toString());
//			LogFactory.getInstance("General").print("Failed to readMessage: " + toString());
//			e.printStackTrace();
//		}
//		return null;
	}
	
	public LinkedBlockingQueue<Message> getInbox(){
		return inbox;
	}
	
	protected void sleep(){
		//Run loops at set speed
		while(System.currentTimeMillis() - lastSleepTime <= RUN_TIME);
		
		lastSleepTime = System.currentTimeMillis();
		
//		try {
//			Thread.sleep(SLEEP_TIME);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	protected void waitForMessage(Message message, Class<? extends StatusUpdateMessage>... messages){
		if(message == null)
			return;
		
		sendMessage(message);
		
		StatusUpdateMessage updateMessage;
		
		while(true){
			for(Message mess : inbox){
				if(mess instanceof StatusUpdateMessage){
					updateMessage = ((StatusUpdateMessage) mess);
					
					if(updateMessage.getCurrentMessage() != null &&
						updateMessage.getCurrentMessage().equals(message) && 
						updateMessage.isDone()){
						
						//Done using it, time to throw it out
						inbox.remove(mess);
						return;
					}
				}else{
					//Not an important message, get rid of it
					inbox.remove(mess);
				}
			}
			
			sleep();
		}
	}
	
	
	public boolean equals(Object obj){
		return this.getClass().getName().equals(obj.getClass().getName());
	}
	
	public abstract String toString();
	
	public abstract void step();
	
	public boolean isDone(){
		return done;
	}
}
