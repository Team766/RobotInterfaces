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
		actors.add(act);
		act.init();
		new Thread(act).start();
	}
	
	public synchronized void remove(Actor actor){
		actor.enabled = false;
		
		//Remove ALL instances of it from list
		for(int i = actors.size() - 1; i >= 0; i--){
			if(actors.get(i).toString().equals(actor.toString()))
				actors.remove(i);
		}
	}
	
	public synchronized void sendMessage(Message newMessage) throws InterruptedException{
		//Add messages to all Actor's queues
		for(Actor act : actors) {
			act.tryAddingMessage(newMessage);
		}
	}
	
	public synchronized Actor getActor(Class<? extends Actor> act){
		for(Actor actor : actors){
			if(actor.getClass().getName().equals(act.getName()))
				return actor;
		}
		return null;
	}
	
	public synchronized void remove(Class<? extends Actor> actor) {
		for(Actor act : actors){
			if(act.equals(actor))
				remove(act);
		}
	}

	public String getCountsPerSecond() {
		String out = "Actors\t\tIterations\n";
		for(Actor clooney : actors){
			out += clooney.toString() + "\t" + clooney.itsPerSec + "\n";
			clooney.itsPerSec = 0;
		}
		return out;
	}
	
}
