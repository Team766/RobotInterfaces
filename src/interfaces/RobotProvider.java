package interfaces;

import java.util.HashMap;

public abstract class RobotProvider {
	
	public static RobotProvider instance;
	
	protected SpeedController[] motors = new SpeedController[12];
	protected EncoderReader[] encoders = new EncoderReader[20];
	protected SolenoidController[] solenoids = new SolenoidController[10];
	protected GyroReader[] gyros = new GyroReader[13];
	protected HashMap<String, CameraReader> cams = new HashMap<String, CameraReader>();
	protected JoystickReader[] joysticks = new JoystickReader[3];
	protected DigitalInputReader[] digInputs = new DigitalInputReader[5];
	
	//HAL
	public abstract SpeedController getMotor(int index);
	
	public abstract EncoderReader getEncoder(int index1, int index2);
	
	public abstract DigitalInputReader getDigitialInputer(int index);
	
	public abstract SolenoidController getSolenoid(int index);
	
	public abstract GyroReader getGyro(int index);
	
	public abstract CameraReader getCamera(String id, String value);
	
	//Operator Devices
	public abstract JoystickReader getJoystick(int index);
	
	public abstract CameraInterface getCamServer();
}
