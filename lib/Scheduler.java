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
		for(Actor a : actors){
			if(a.toString().equals(act.toString())){
				System.err.println("Scheduler: " + act + " already added to schedueler");
				return;
			}
		}
		actors.add(act);
		act.init();
	}
	
	public synchronized void remove(Actor actor){
		//Remove ALL instances of it from list
		for(int i = actors.size() - 1; i >= 0; i--){
			if(actors.get(i).toString().equals(actor.toString())){
				actors.remove(i);
			}
		}
	}
	
	public synchronized void run() {
		for (Actor act : actors) {
			if (!act.done) {
				act.iterate();
			}
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
		for(int i = actors.size() - 1; i >= 0; i--){
			if (actor.isInstance(actors.get(i))) {
				actors.remove(i);
			}
		}
	}
	
	/**
	 * Empties entire list of actors
	 * 
	 * Used so tests get reset between each one.
	 */
	public synchronized void reset(){
		actors.clear();
		instance = null;
	}
}
