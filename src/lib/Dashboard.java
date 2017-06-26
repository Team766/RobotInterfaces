package lib;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static lib.LogMessage.Level;

/**
 * This actor handles all interfacing with the custom dashboard. It opens a TCP
 * server on port {@value PORT}, and accepts a single connection. (If that connection
 * is closed/lost, a new one will be accepted.) {@link DashboardMessage}s are
 * generated when messages are received from the dashboard. Various methods are
 * also provided for sending messages to the dashboard.
 * <p>
 * This class is a singleton. Access its instance via {@link #getInstance()
 * Dashboard.getInstance()}.
 * <p>
 * See <a href="https://github.com/Team766/FRC-Dashboard">Team766/FRC-Dashboard on GitHub</a>
 *
 * @author Quinn Tucker
 */
public class Dashboard extends Actor {
	
	private static Dashboard _instance;
	
	/**
	 * Gets the single {@code Dashboard} instance, creating it
	 * if it hasn't been already.
	 *
	 * @return the {@code Dashboard} instance
	 */
	public static synchronized Dashboard getInstance() {
		if (_instance == null) _instance = new Dashboard();
		return _instance;
	}
	
	/**
	 * {@code Dashboard}'s constructor is private; use {@link #getInstance()}.
	 */
	private Dashboard() {}
	
	
	private static final int PORT = 5801;
	
	private ServerSocket serverSocket;
	private Socket socket;
	private Writer out;
	private Reader in;
	
	private StringBuilder inBuf;
	private String msgType;
	private int msgLen = -1;
	private List<String> msgArgs;
	
	@Override
	public void init() {
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (enabled) {
			// accept a connection if not already connected
			try {
				if (socket == null || socket.isClosed()) {
					socket = serverSocket.accept();
					out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
					in = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
					inBuf = new StringBuilder();
				}
			} catch (IOException e) {
				log(Level.ERROR, "Dashboard server failed to accept a connection: " + e);
				sleep();
				continue;
			}
			
			// read a character from the socket
			try {
				int ch = in.read();
				if (ch == -1 || ch == ':') { // end of a token (EoF or colon)
					processToken(inBuf.toString());
					inBuf.setLength(0); // clear the buffer
				} else {
					inBuf.appendCodePoint(ch);
				}
			} catch (IOException e) {
				log(Level.ERROR, "Dashboard server failed to read data: " + e);
			}
		}
	}
	
	private void processToken(String token) {
		if (msgType == null) {
			msgType = token;
		} else if (msgLen == -1) {
			try {
				msgLen = Integer.parseInt(token);
				if (msgLen < 0) throw new NumberFormatException();
				msgArgs = new ArrayList<>();
			} catch (NumberFormatException e) {
				log(Level.ERROR, "Dashboard sent bad argument count: \""+token+"\"");
			}
		} else {
			msgArgs.add(token);
			msgLen--;
		}
		if (msgLen == 0) {
			sendMessage(new DashboardMessage(msgType, msgArgs));
			msgType = null;
			msgLen = -1;
			msgArgs = null;
		}
	}
	
	@Override
	public void step() {}
	
	@Override
	public void iterate() {}
	
}
