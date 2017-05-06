package lib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Handles saving log messages to the disk
 * 
 * Format:
 * LEVEL TIME LOCATION: MESSAGE
 * 
 * Level: INFO, DEBUG, WARNING, ERROR, FATAL
 * Time: System time of the robot since being initialized
 * Location: Stack trace of the line which called the log message
 * Message: Actual contents of message to be displayed
 */
public class LogHandler extends Actor {

	private final int WAIT_TIME = 50;  //	1 / (Hz) --> to milliseconds
	
	String fileName;
	
	Message currentMessage;
	
	public LogHandler(String file){
		fileName = file;
	}
	
	@Override
	public void init() {
		acceptableMessages = new Class[]{LogMessage.class};
	}
	
	@Override
	public void run() {
		while(enabled){
			iterate();
			sleep(WAIT_TIME);
		}	
	}
	
	@Override
	public String toString() {
		return "Actor: LogHandler";
	}

	@Override
	public void iterate() {
		if(newMessage()){
			currentMessage = readMessage();
			
			if(currentMessage == null)
				return;
			
			if(currentMessage instanceof LogMessage){
				logError(((LogMessage)currentMessage).getMessage());
			}
		}
	}
	
	private void logError(String message){		
		try(PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))){
			writer.print(message);
			writer.println();
		}catch(IOException e){
			e.printStackTrace();
			createFile();
			
			sleep();
			logError(message);
		}
	}
	
	private void createFile(){
		try{
			File file = new File(fileName);
			if(!file.exists())
				file.createNewFile();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void step() {
	}
	
}
