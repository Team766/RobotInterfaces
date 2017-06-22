package lib;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
	
	private static Scheduler instance;
	
	private List<Actor> actors;
	
	public static synchronized Scheduler getInstance() {
		if (instance == null)
			instance = new Scheduler();
		
		return instance;
	}
	
	private Scheduler() {
		actors = new ArrayList<>();
	}
	
	public synchronized void add(Actor actor) {
		add(actor, 100);
	}
	
	public synchronized void add(Actor actor, int rateHz) {
		if (actors.contains(actor)) {
			System.err.println("Scheduler: " + actor + " already added to schedueler");
			return;
		}
		actor.enabled = true;
		actor.setSleepTime((long)((1.0/rateHz) * 1000.0));
		actors.add(actor);
		actor.init();
		new Thread(actor).start();
	}
	
	public synchronized void remove(Actor actor) {
		actor.enabled = false;
		
		//Remove ALL instances of it from list
		actors.removeIf(a -> a.equals(actor));
	}
	
	public synchronized void remove(Class<? extends Actor> type) {
		actors.removeIf(actor -> {
			boolean remove = type.isAssignableFrom(actor.getClass());
			if (remove) actor.enabled = false;
			return remove;
		});
	}
	
	public synchronized void sendMessage(Message newMessage) throws InterruptedException {
		//Add messages to all Actor's queues
		for (Actor act : actors) {
			act.tryAddingMessage(newMessage);
		}
	}
	
	public synchronized Actor getActor(Class<? extends Actor> type) {
		for (Actor actor : actors) {
			if (type.isAssignableFrom(actor.getClass()))
				return actor;
		}
		return null;
	}
	
	
	/**
	 * Empties entire list of actors
	 *
	 * Used so tests get reset between each one.
	 */
	public synchronized void reset() {
		//Kill all current actors
		for (Actor clooney : actors) {
			clooney.enabled = false;
		}
		
		actors.clear();
		instance = null;
	}
	
	public String getCountsPerSecond() {
		String out = "Actors\t\tIterations\n";
		for (Actor clooney : actors) {
			out += clooney.toString() + "\t" + clooney.itsPerSec + "\n";
			clooney.itsPerSec = 0;
		}
		return out;
	}
	
}
