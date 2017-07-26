package lib;

/**
 * @author Quinn Tucker
 */
public class TargetController {
	
	private double k_Speed;
	private double k_MT;
	private double k_Friction;
	
	private double target;
	private double startPos, stopPoint;
	
	private double pos, vel, acc;
	private double lastUpdateTime;
	
	private boolean isFirstUpdate = true;
	private boolean reversed;
	private boolean done = false;
	
	private double output;
	
	public TargetController(double target, double k_Speed, double k_MT, double k_Friction) {
		this.target = target;
		this.k_Speed = k_Speed;
		this.k_MT = k_MT;
		this.k_Friction = k_Friction;
	}
	
	public double update(double currentPos) {
		// on the first call to update(), set up the initial state
		// (this is dependent on the initial position passed in)
		if (isFirstUpdate) {
			startPos = currentPos;
			reversed = startPos > target;
			if (reversed) {
				// if the target is in the negative direction, invert
				// the input & output so that internal inequalities
				// and such still work as intended
				target = -target;
				startPos = -startPos;
			}
			stopPoint = (target+startPos)/2;
			lastUpdateTime = getTime();
			
			isFirstUpdate = false;
			return output = 0.0;
		} else if (done) {
			return output; // we're done; just return the last output (0.0)
		}
		
		// calculate delta time (dt) since the last update
		double curTime = getTime();
		double dt = curTime - lastUpdateTime;
		if (dt < 0.001) return output; // called too rapidly; wait to get a more accurate delta
		lastUpdateTime = curTime;
		
		// compute the current velocity and acceleration
		if (reversed) currentPos = -currentPos;
		double lastVel = vel;
		vel = (currentPos - pos) / dt;
		pos = currentPos;
		acc = (vel - lastVel) / dt;
		
		if (pos < stopPoint) {
			// before we reach the stop point, go full speed ahead
			return output = 1.0;
		} else if (pos < target) {
			// after the stop point, control the output
			double dist = target - pos;
			output = vel*k_Speed - (vel*vel)*k_MT/(dist*2) - k_Friction*k_MT;
			if (reversed) output = -output;
			return output;
		} else {
			// once we're at/past the target, stop and finish
			double stopTime = -vel/acc;
			if (stopTime > 0.05) { // make sure we've come to a complete stop
				return output = reversed? 0.5 : -0.5; // half speed backward
			} else {
				done = true;
				return output = 0.0;
			}
		}
	}
	
	public double getOutput() {
		return output;
	}
	
	public boolean isDone() {
		return done;
	}
	
	
	private static double getTime() {
		return System.nanoTime() / 1000000000.0;
	}
	
}
