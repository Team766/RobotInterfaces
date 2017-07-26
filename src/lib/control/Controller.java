package lib.control;

/**
 * @author Quinn Tucker
 */
public abstract class Controller {
	
	protected double output;
	
	protected double dtMin;
	protected double dt, pos, vel, acc;
	private double lastUpdateTime;
	
	protected boolean isFirstUpdate = true;
	protected boolean done = false;
	protected boolean reversed = false;
	
	protected Controller() {
		this(0.0);
	}
	
	protected Controller(double dtMin) {
		this.dtMin = dtMin;
	}
	
	protected double doFirstUpdate() {
		return doUpdate();
	}
	
	protected abstract double doUpdate();
	
	public double update(double currentPos) {
		if (reversed) currentPos = -currentPos;
		
		if (isFirstUpdate) {
			// on the first call to update(), set up any initial state
			// (which may be dependent on the initial position passed in)
			lastUpdateTime = getTime();
			pos = currentPos;
			dt = vel = acc = 0.0;
			output = doFirstUpdate();
			isFirstUpdate = false;
		} else if (done) {
			return output; // we're done; just return the last output
		} else {
			// calculate delta time (dt) since the last update
			double curTime = getTime();
			dt = curTime - lastUpdateTime;
			if (dt < dtMin) return output; // called too rapidly; wait to get a more accurate delta
			lastUpdateTime = curTime;
			
			// compute the current velocity and acceleration
			double lastVel = vel;
			vel = (currentPos - pos) / dt;
			pos = currentPos;
			acc = (vel - lastVel) / dt;
			
			// update the output value
			output = doUpdate();
		}
		
		if (reversed) output = -output;
		return output;
	}
	
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
