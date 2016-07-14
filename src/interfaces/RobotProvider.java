package interfaces;

public abstract class RobotProvider {
	
	public static RobotProvider instance;
	
	
	//HAL
	public abstract SpeedController getLeftDrive();
	public abstract SpeedController getRightDrive();
	
	public abstract EncoderReader getLeftEncoder();
	public abstract EncoderReader getRightEncoder();
	
	public abstract SolenoidController getDriveShifter();
	
	public abstract GyroReader getGyro();
	
	//Operator Devices
	public abstract JoystickReader getLeftJoystick();
	public abstract JoystickReader getRightJoystick();
	public abstract JoystickReader getBoxJoystick();
}
