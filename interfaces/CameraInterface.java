package interfaces;

import org.opencv.core.Mat;

public interface CameraInterface {	
	public void startAutomaticCapture();
	public void getFrame(Mat img);
	public void putFrame(Mat img);
}
