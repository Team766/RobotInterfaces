package lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Send messages to robot
 */
public abstract class MessageServer extends Actor {

	private ServerSocket serverSocket;
	private Socket socket;
	private BufferedReader in;
	private String[] message;

	public MessageServer(int port) {
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (Exception e) {
			e.printStackTrace();
			log("Failed to init server");
		}
	}
	
	public void connect(){
		try {
			socket = serverSocket.accept();
		} catch (IOException e1) {
			System.out.println("Failed to accept socket");
			log("Failed to accept socket");
			return;
		}
		log("Accepted new connection");
		
		
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(IOException e){
			log("Failed to init bufferdReader for message server");
		}
	}

	public ServerMessage grabData(){
		String input;
		try {
			input = in.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
			log("Failed to get input");
			return null;
		}
		if (input == null) {
			log("Got input, but it was null :(");
			return null;
		}
		
		//MessageName val1 val2 val3
		message = input.split(" ");
		
		return new ServerMessage(message[0], Arrays.copyOfRange(message, 1, message.length));
	}
	
	public void closeSockets(){
		try {
			serverSocket.close();
			socket.close();
		} catch (IOException e) {}
	}
	
	
	public String toString() {
		return "Lib:\tMessageServer";
	}
}