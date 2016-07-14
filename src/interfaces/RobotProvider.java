package interfaces;

public abstract class RobotProvider {
	
	public static RobotProvider instance;
	
	
	//HAL
	public abstract SpeedController getMotor(int index);
	
	public abstract EncoderReader getEncoder(int index);
	
	public abstract SolenoidController getSolenoid(int index);
	
	public abstract GyroReader getGyro(int index);
	
	//Operator Devices
	public abstract JoystickReader getJoystick(int index);
}
