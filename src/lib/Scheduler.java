package lib;

import java.util.ArrayList;

public class Scheduler {
	
	private static Scheduler instance;
	
	private ArrayList<Actor> actors;
	
	public static synchronized Scheduler getInstance(){
		if(instance == null)
			instance = new Scheduler();
		
		return instance;
	}
	
	private Scheduler(){
		actors = new ArrayList<Actor>();
	}
	
	public synchronized void add(Actor act){
		add(act, 100);
	}
	
	public synchronized void add(Actor act, int rateHz){
		if (actors.contains(act)) {
			System.err.println("Scheduler: " + act + " already added to scheduler");
			return;
		}
		act.enabled = true;
		act.setSleepTime(1000L / rateHz);
		actors.add(act);
		act.init();
		new Thread(act).start();
	}
	
	public synchronized void remove(Actor actor){
		actor.enabled = false;
		
		//Remove ALL instances of it from list
		actors.removeIf(actor::equals);
	}
	
	public synchronized void sendMessage(Message newMessage) throws InterruptedException{
		//Add messages to all Actor's queues
		for(Actor act : actors) {
			act.tryAddingMessage(newMessage);
		}
	}
	
	public synchronized Actor getActor(Class<? extends Actor> type){
		for(Actor actor : actors){
			if(actor.getClass().equals(type))
				return actor;
		}
		return null;
	}
	
	public synchronized void remove(Class<? extends Actor> type) {
		// remove all matching actors from the list
		actors.removeIf(actor -> type.isAssignableFrom(actor.getClass()));
	}
	
	
	/**
	 * Empties entire list of actors
	 * 
	 * Used so tests get reset between each one.
	 */
	public synchronized void reset(){
		//Kill all current actors
		for(Actor clooney : actors){
			clooney.enabled = false;
		}
		
		actors.clear();
		instance = null;
	}

	public String getCountsPerSecond() {
		StringBuilder out = new StringBuilder("Actors\t\tIterations\n");
		for(Actor clooney : actors){
			out.append(clooney.toString()).append('\t').append(clooney.itsPerSec).append('\n');
			clooney.itsPerSec = 0;
		}
		return out.toString();
	}
	
}
