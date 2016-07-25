package interfaces;

public abstract class RobotProvider {
	
	public static RobotProvider instance;
	
	protected SpeedController[] motors = new SpeedController[10];
	protected EncoderReader[] encoders = new EncoderReader[10];
	protected SolenoidController[] solenoids = new SolenoidController[10];
	protected GyroReader[] gyros = new GyroReader[5];
	protected JoystickReader[] joysticks = new JoystickReader[3];
	
	//HAL
	public abstract SpeedController getMotor(int index);
	
	public abstract EncoderReader getEncoder(int index1, int index2);
	
	public abstract SolenoidController getSolenoid(int index);
	
	public abstract GyroReader getGyro(int index);
	
	//Operator Devices
	public abstract JoystickReader getJoystick(int index);
}
