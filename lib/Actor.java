package lib;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class Actor {
	protected boolean done = false;
	
	public Class<? extends Message>[] acceptableMessages = (Class<? extends Message>[])new Class[]{};
	private LinkedBlockingQueue<Message> inbox = new LinkedBlockingQueue<Message>();
	
	public Class<? extends Actor>[] actorHierarchy = (Class<? extends Actor>[])new Class[]{};
	
	public void init() {}
	
	public int countMessages(Class<? extends Message> messages){
		int sum = 0;
		for(Message m : inbox.toArray(new Message[0])){
			if(messages.isInstance(m))
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
			if(message.isInstance(m)){
				return true;
			}
		}
		//logDebug("Message rejected: " + message.getName());
		//System.out.println(toString() + " Rejected: " + m.toString());
		return false;
	}

	public void tryAddingMessage(Message m){
		if(keepMessage(m)) {
	   	  	try {
				inbox.put(m);
			} catch (InterruptedException e) {
				e.printStackTrace();
				logError("Failed to add Message:\t" + m);
			}
		}
	}
	
	public void sendMessage(Message mess){
		try {
			Scheduler.getInstance().sendMessage(mess);
		} catch (InterruptedException e) {
			System.err.println("Failed to send message: " + toString());
			logError("Actor: Failed to send message: " + toString());
			e.printStackTrace();
		}
	}
	
	public Message readMessage(){
		return inbox.poll();
	}
	
	public LinkedBlockingQueue<Message> getInbox(){
		return inbox;
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}

	protected void logDebug(String message) {
		log(Logger.Level.DEBUG, message, 1);
	}

	protected void logInfo(String message) {
		log(Logger.Level.INFO, message, 1);
	}

	protected void logWarning(String message) {
		log(Logger.Level.WARNING, message, 1);
	}

	protected void logError(String message) {
		log(Logger.Level.ERROR, message, 1);
	}

	protected void log(Logger.Level lvl, String message) {
		log(lvl, message, 1);
	}

	private void log(Logger.Level lvl, String message, int stackLevel) {
		LogFactory.getInstance(getName()).log(
			lvl,
			String.format("[%s] %s: %s", getName(), getCallingClass(stackLevel + 1), message));
	}
	
	// stackLevel is the index into the callstack that should be examined.
	// stackLevel=0 returns the name of the class that called this method, stackLevel=1 returns
	// the name of the class that called the method at stackLevel=0, etc.
	private String getCallingClass(int stackLevel) {
		Object[] out = Thread.currentThread().getStackTrace();
		// Add one for getStackTrace() and one for getCallingClass()
		stackLevel += 2;
		if (out.length <= stackLevel) {
			return "<Unknown>";
		}
		return out[2 + stackLevel].toString();
	}
	
	@Override
	public boolean equals(Object obj){
		return this.getClass().getName().equals(obj.getClass().getName());
	}
	
	@Override
	public abstract String toString();
	
	public abstract void iterate();
	
	public boolean isDone(){
		return done;
	}
}
