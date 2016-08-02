package lib;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class Actor implements Runnable{
	
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
	
	protected boolean keepMessage(Message m){
	    for(Class<? extends Message> message : acceptableMessages){
	        if(m.getClass().equals(message)){
	            return true;
	        }
	    }
	    return false;
	}

	public void tryAddingMessage(Message m){
	    if(keepMessage(m)){
	        try {
	            inbox.put(m);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	public void sendMessage(Message mess){
		try {
			Scheduler.getInstance().sendMessage(mess);
		} catch (InterruptedException e) {
			System.err.println("Failed to send message: " + toString());
			e.printStackTrace();
		}
	}
	
	public Message readMessage(){
		try {
			return inbox.take();
		} catch (InterruptedException e) {
			System.err.println("Failed to readMessage: " + toString());
			e.printStackTrace();
		}
		return null;
	}
	
	public LinkedBlockingQueue<Message> getInbox(){
		return inbox;
	}
	
	protected void sleep(){
		try {
			Thread.sleep(15);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public abstract String toString();
	
}
