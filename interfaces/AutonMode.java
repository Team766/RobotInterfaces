package interfaces;

public interface AutonMode {
	
	public void iterate();
	
	public void commandDone(boolean done);
	
	public String getTarget();
	
}
