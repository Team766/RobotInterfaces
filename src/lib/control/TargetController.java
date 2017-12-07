package lib.control;

import lib.Dashboard;

/**
 * @author Quinn Tucker
 */
public class TargetController extends Controller {
	
	private double k_Speed;
	private double k_MT;
	private double k_Friction;
	
	private double target;
	private double stopPoint;
	
	private double maxAcc = -Double.MAX_VALUE;
	
	public TargetController(double target, double k_Speed, double k_MT, double k_Friction) {
		super(0.001);
		this.target = target;
		this.k_Speed = k_Speed;
		this.k_MT = k_MT;
		this.k_Friction = k_Friction;
	}
	
	@Override
	protected double doFirstUpdate() {
		reversed = pos > target;
		if (reversed) {
			// if the target is in the negative direction, invert
			// the input & output so that internal inequalities
			// and such still work as intended
			target = -target;
			pos = -pos;
		}
		stopPoint = (target+pos)/2;
		return 0.0;
	}
	
	CalibrationController.Average velAvg = new CalibrationController.Average(15);
	CalibrationController.Average accAvg = new CalibrationController.Average(15);
	
	@Override
	protected double doUpdate() {
		Dashboard.plotData("pos-target", pos-target);
		velAvg.add(vel);
		final double vel = velAvg.average();
		Dashboard.plotData("vel", vel);
//		accAvg.add(acc);
//		final double acc = accAvg.average();
		Dashboard.plotData("acc", acc);
		
		if (acc > maxAcc) maxAcc = acc;
		if (Math.abs(target-pos) < (target-stopPoint)*2*0.1 && Math.abs(acc) < maxAcc*0.01) {
			done = true;
			return 0.0;
		}
		
		if (pos < stopPoint) {
			// before we reach the stop point, go full speed ahead
			return 1.0;
		} else if (pos < target) {
			// after the stop point, control the output
			double dist = target - pos;
			return vel*k_Speed - (vel*vel)*k_MT/(dist*2) - k_Friction*k_MT;
		} else {
			// once we're at/past the target, stop and finish
			double stopTime = -vel/acc;
			if (stopTime > 0.05) { // make sure we've come to a complete stop
				return -0.5; // half speed backward
			} else {
				done = true;
				return 0.0;
			}
		}
	}
	
}
