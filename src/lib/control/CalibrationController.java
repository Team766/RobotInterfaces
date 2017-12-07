package lib.control;

import lib.Dashboard;
import lib.LogMessage;
import lib.Scheduler;

import java.util.ArrayList;

/**
 * @author Quinn Tucker
 */
public class CalibrationController extends Controller {
	
	private int stage = 0;
	private double stageStart;
	
	private double s1, V1;
	private double a1, va1;
	
	public double kSpeed, kMT, kFriction;
	
	public CalibrationController() {
		super(1.0/60);
	}
	
	private void nextStage() {
		stage++;
		stageStart = getTime();
	}
	
	@Override
	protected double doFirstUpdate() {
		log("Calibrating constants...");
		stageStart = getTime();
		return super.doFirstUpdate();
	}
	
	static class Average {
		int num;
		ArrayList<Double> list = new ArrayList<>();
		public Average(int num) {this.num = num;}
		public void add(double x) {
			list.add(x);
			if (list.size() > num)
				list.remove(0);
		}
		public double average() {
			double total = 0.0;
			for (double x : list) total += x;
			return total / list.size();
		}
	}
	Average accAvg = new Average(15);
	Average velAvg = new Average(15);
	
	@Override
	protected double doUpdate() {
		double stageTime = getTime() - stageStart;
//		log("dt="+dt+", pos="+pos+", vel="+vel+", acc="+acc+",  stageTime="+stageTime);
		accAvg.add(acc);
		final double acc = accAvg.average();
		velAvg.add(vel);
		final double vel = velAvg.average();
		log("vel*kSpeed = "+(vel*kSpeed));
		Dashboard.plotData("pos", pos);
		Dashboard.plotData("vel", vel);
		Dashboard.plotData("acc", acc);
		
		boolean upToSpeed = stageTime > 0.5 && vel > 0 && acc/vel < 0.09;
		switch (stage) {
			case 0:
				// measure the max speed at 0.5 power
				if (upToSpeed) {
					s1 = vel;
					V1 = output;
					log("Measurement 1");
					nextStage();
				}
				return 0.5;
			case 1:
				// measure the max speed at 1.0 power, and compute kSpeed
				if (upToSpeed) {
					double s2 = vel;
					double V2 = output;
					kSpeed = (V1-V2)/(s1-s2);
					log("Measurement 2; kSpeed = "+kSpeed);
					nextStage();
				}
				return 1.0;
			
			case 2:
				// slow back down for the next measurement
				if (vel < 0.1/kSpeed) nextStage();
				return 0.0;
			
			case 3:
				// accelerate and take a measurement
				if (vel > 0.3/kSpeed) {
					a1 = acc;
					va1 = output - vel*kSpeed;
					log("Measurement 3");
					nextStage();
				}
				return 1.0;
			case 4:
				// decelerate, take a measurement, and compute kMT & kFriction
				if (vel < 0.15/kSpeed) {
					double a2 = acc;
					double va2 = output - vel*kSpeed;
					kMT = (va1-va2)/(a1-a2);
					kFriction = a1 - va1/kMT;
					log("Measurement 4; kMT = "+kMT+", kFriction = "+kFriction);
					nextStage();
					done = true;
				}
				return 0.0;
			
			default:
				// should never be reached
				log(LogMessage.Level.ERROR, "CalibrationController.doUpdate(): Bad stage value: "+stage);
				return 0.0;
		}
	}
	
	private static void log(String msg) {
		log(LogMessage.Level.DEBUG, msg);
	}
	
	private static void log(LogMessage.Level level, String msg) {
		try {
			Scheduler.getInstance().sendMessage(new LogMessage(level, msg));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
