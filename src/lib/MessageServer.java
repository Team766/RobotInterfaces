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
public class MessageServer extends Actor {
	private static final int PORT = 5801;

	private ServerSocket serverSocket;
	private Socket socket;
	private BufferedReader in;
	private String[] message;

	public MessageServer() {
		try {
			this.serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
			log("Failed to init server");
		}
	}

	public void run() {
		try {
			socket = serverSocket.accept();
		} catch (IOException e1) {
			System.out.println("Failed to accept socket");
			log("Failed to accept socket");
		}
		
		System.out.println("Accepted new connection");
		
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(Exception e){
			System.out.println("Error");
		}
		
		while(enabled){
			iterate();
			sleep();
		}
	}

	@Override
	public void init() {
	}

	@Override
	public String toString() {
		return "Actor: \tMessageServer";
	}

	@Override
	public void step() {
	}

	@Override
	public void iterate() {
		String input;
		try {
			input = in.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
			log("Failed to get input");
			return;
		}
		if (input == null) {
			return;
		}
		try {
			//MessageName val1 val2 val3
			message = input.split(" ");
			
			Scheduler.getInstance().sendMessage(new ServerMessage(message[0], Arrays.copyOfRange(message, 1, message.length)));
			
		} catch (Exception e) {
			System.out.println("ERROR in Message Server");
		}finally {
			try {
				socket.close();
			} catch (IOException e) {}
		}
	}
}