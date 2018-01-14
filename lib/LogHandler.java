package lib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Arrays;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Handles saving log messages to the disk
 * 
 * Format: LEVEL TIME LOCATION: MESSAGE
 * 
 * Level: INFO, DEBUG, WARNING, ERROR, FATAL Time: System time of the robot
 * since being initialized Location: Stack trace of the line which called the
 * log message Message: Actual contents of message to be displayed
 */
public class LogHandler extends Actor {

	private final int BUFFER_SIZE = 500; //500

	String fileName, message;

	CircularBuffer messageBuffer;

	Message currentMessage;

	HttpServer server;

	public LogHandler(String file) {
		fileName = file;
	}

	@Override
	public void init() {
		messageBuffer = new CircularBuffer(BUFFER_SIZE);

		acceptableMessages = new Class[] {LogMessage.class};
		message = "";

		try {
			server = HttpServer.create(new InetSocketAddress(5800), 0);

			/*
			 * Stores buffer of past messages
			 * 
			 * Receives time from log_displayer of the most recent log message
			 * it received. The robot's server then returns all the messages
			 * that have occurred since then.
			 */
			@SuppressWarnings("unused")
			HttpContext context = server.createContext("/", new HttpHandler() {
				public void handle(HttpExchange exchange) throws IOException {
					try{
						// mostRecentTimeStamp = 00_00_00
						String mostRecentTimeStamp = exchange.getRequestURI().toString().substring(1);
						
						messageBuffer.removeOldMessages(timeInSecs(mostRecentTimeStamp));
						
	//					System.out.println("TimeStamp: " + exchange.getRequestURI().toString());
	//					messageBuffer.printTimes();
	
						String response = messageBuffer.stackArrayElements();
	//					System.out.println("resonse: " + response);
						//System.out.println(messageBuffer);
						exchange.sendResponseHeaders(200,response.getBytes().length);
						OutputStream os = exchange.getResponseBody();
						os.write(response.getBytes());
						os.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});

			server.start();
		} catch (IOException e) {
			System.out.println("HTTP log server failed to open");
		}
	}
	
	/**
	 * Takes a time and returns the time in seconds
	 * 
	 * @param time - unix time as string
	 * @return time in seconds
	 */
	private long timeInSecs(String time){
		return Long.parseLong(time) / 1000l;
	}
	

	@Override
	public void run() {
		while (enabled) {
			iterate();
			sleep();
		}
	}

	@Override
	public String toString() {
		return "Actor: LogHandler";
	}

	@Override
	public void iterate() {
		if (newMessage()) {
			currentMessage = readMessage();

			if (currentMessage == null)
				return;

			if (currentMessage instanceof LogMessage) {
				message = ((LogMessage) currentMessage).getMessage();
				messageBuffer.add(message);
				logError(message);
			}
		}
	}

	private void logError(String message) {
		try (PrintWriter writer = new PrintWriter(
				new FileWriter(fileName, true))) {
			writer.print(message);
			writer.println();
		} catch (IOException e) {
			e.printStackTrace();
			createFile();

			sleep();
			logError(message);
		}
	}

	private void createFile() {
		try {
			File file = new File(fileName);
			if (!file.exists())
				file.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void step() {
	}

}
