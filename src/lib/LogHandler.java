package lib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

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
	
	String fileName, message;
	
	Message currentMessage;
	
	HttpServer server;
	
	public LogHandler(String file){
		fileName = file;
	}
	
	@Override
	public void init() {
		acceptableMessages = new Class[]{LogMessage.class};
		message = "";
		
		try {
			server = HttpServer.create(new InetSocketAddress(5800), 0);
		
		
			@SuppressWarnings("unused")
			HttpContext context = server.createContext("/", new HttpHandler(){
				public void handle(HttpExchange exchange) throws IOException {
					String response = message;
					
					exchange.sendResponseHeaders(200, response.getBytes().length);
					OutputStream os = exchange.getResponseBody();
					os.write(response.getBytes());
					os.close();
	    	    }
			});
			
		server.start();
		} catch (IOException e) {
			System.out.println("HTTP log server failed to open");
		}
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
				message = ((LogMessage)currentMessage).getMessage(); 
				logError(message);
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
