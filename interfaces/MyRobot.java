package interfaces;
// Team 766 - Robot Interface Base class

public interface MyRobot {
	public void robotInit();
	public void disabledInit();
	public void autonomousInit();
	public void teleopInit();
	public void testInit();
	
	public void disabledPeriodic();
	public void autonomousPeriodic();
	public void teleopPeriodic();
	public void testPeriodic();
	
	public void startCompetition();
}
