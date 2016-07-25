package interfaces;

public interface JoystickReader {
	 /**
	   * Get the value of the axis.
	   *
	   * @param axis The axis to read, starting at 0.
	   * @return The value of the axis.
	   */
	  public double getRawAxis(final int axis);
	  
	  /**
	   * Get the button value (starting at button 1)
	   *
	   * The appropriate button is returned as a boolean value.
	   *
	   * @param button The button number to be read (starting at 1).
	   * @return The state of the button.
	   */
	  public boolean getRawButton(final int button);
}