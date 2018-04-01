package interfaces;

public interface AutonMode {
	
	public void iterate();
	
	public void driveCommandDone(boolean done);
	
	public void shoulderCommandDone(boolean done);
	
	public String getTarget();
	
}
