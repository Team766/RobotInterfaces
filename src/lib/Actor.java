package lib;

import interfaces.HighPriorityMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Actor implements Runnable {
	
	private long runTime = 10;	//	1 / (Hz) --> to milliseconds
	private static final long MIN_SLEEP_TIME = 1;
	
	private static final int MAX_MESSAGES = 15;
	private long lastSleepTime;
	
	public double itsPerSec = 0;
	protected boolean done = false;
	
	protected List<Class<? extends Message>> acceptableMessages = new ArrayList<>();
	private LinkedBlockingQueue<Message> inbox = new LinkedBlockingQueue<>(MAX_MESSAGES);
	
	public List<Class<? extends Message>> actorHierarchy = new ArrayList<>();
	
	public boolean enabled = true;
	
	public Actor() {
		lastSleepTime = System.currentTimeMillis();
	}
	
	public abstract void init();
	
	@SafeVarargs
	protected final void setMessageTypes(Class<? extends Message>... types) {
		acceptableMessages.clear();
		Collections.addAll(acceptableMessages, types);
	}
	
	public void filterMessages(){
	}
	
	public int countMessages(Class<? extends Message> type) {
		int count = 0;
		for (Message m : inbox) {
			if (type.isAssignableFrom(m.getClass()))
				count++;
		}
		return count;
	}
	
	protected boolean newMessage() {
		return !inbox.isEmpty();
	}
	
	public void clearInbox() {
		inbox.clear();
	}
	
	private boolean keepMessage(Message m) {
		return acceptableMessages.contains(m.getClass());
	}
	
	public void tryAddingMessage(Message m) {
		if (keepMessage(m)) {
			// Check for room in inbox
			if (inbox.size() >= MAX_MESSAGES) {
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
	
	private void removeMessage() {
		for (Message m : inbox) {
			if (!(m instanceof HighPriorityMessage)) {
				//System.out.println("Removing: " + mess.toString());
				inbox.remove(m);
				return;
			}
		}
		LogFactory.getInstance("General").print("Failed to remove a message, all high priority");
	}
	
	public void sendMessage(Message mess) {
		try {
			Scheduler.getInstance().sendMessage(mess);
		} catch (InterruptedException e) {
			System.err.println("Failed to send message: " + toString());
			LogFactory.getInstance("General").print("Actor: Failed to send message: " + toString());
			e.printStackTrace();
		}
	}
	
	protected Message readMessage() {
		return inbox.poll();
	}
	
	public LinkedBlockingQueue<Message> getInbox() {
		return inbox;
	}
	
	protected void sleep() {
		sleep(runTime);
	}
	
	protected void sleep(long sleepTime) {
		//Run loops at set speed
		try {
			//System.out.println("Curr: " + System.currentTimeMillis() + "\tLast: " + lastSleepTime);
			Thread.sleep(sleepTime - (System.currentTimeMillis() - lastSleepTime));
		} catch (InterruptedException e) {
			System.out.println(toString() + "\tNo time to sleep, running behind schedule!!");
			try {
				Thread.sleep(MIN_SLEEP_TIME);
			} catch (InterruptedException e1) {}
			Thread.currentThread().interrupt(); // preserve interrupted status
		}
		
		lastSleepTime = System.currentTimeMillis();
	}
	
	@SafeVarargs
	protected final void waitForMessage(Message message, Class<? extends StatusUpdateMessage>... types){
		if (message == null) return;
		
		sendMessage(message);
		
		while (enabled) {
			for (Message mess : inbox) {
				if (mess instanceof StatusUpdateMessage) {
					StatusUpdateMessage updateMessage = (StatusUpdateMessage) mess;
					
					// TODO: check if updateMessage matches types
					if (message.equals(updateMessage.getCurrentMessage()) &&
						updateMessage.isDone()) {
						
						//Done using it, time to throw it out
						inbox.remove(mess);
						return;
					}
				} else {
					//Not an important message, get rid of it
					inbox.remove(mess);
				}
			}
			
			sleep();
		}
	}
	
	protected void log(String message) {
		sendMessage(new LogMessage(getSourceClass() + ": " + message));
	}
	
	private String getSourceClass() {
		Object[] out = Thread.currentThread().getStackTrace();
		return out[out.length - 2].toString();
	}
	
	public boolean equals(Object obj) {
		return obj != null && this.getClass().equals(obj.getClass());
	}
	
	public abstract String toString();
	
	public abstract void step();
	
	public abstract void iterate();
	
	public boolean isDone() {
		return done;
	}
	
	public void setSleepTime(long time) {
		runTime = time;
	}
	
}
