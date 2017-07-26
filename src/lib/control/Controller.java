package lib.control;

/**
 * @author Quinn Tucker
 */
public abstract class Controller {
	
	protected double output;
	
	protected boolean done = false;
	
	public abstract double update(double currentPos);
	
	public double getOutput() {
		return output;
	}
	
	public boolean isDone() {
		return done;
	}
	
	
	public static double getTime() {
		return System.nanoTime() / 1000000000.0;
	}
	
}
