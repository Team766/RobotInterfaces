package interfaces;

public abstract class RobotProvider {
	
	public static RobotProvider instance;
	
	public abstract SpeedController getLeftDrive();
	public abstract SpeedController getRightDrive();
	
	public abstract EncoderReader getLeftEncoder();
	public abstract EncoderReader getRightEncoder();
	
	public abstract SolenoidController getDriveShifter();
	
	public abstract GyroReader getGyro();
}
