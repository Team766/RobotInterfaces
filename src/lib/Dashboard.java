package lib;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static lib.LogMessage.Level;

/**
 * This actor handles all interfacing with the custom dashboard.
 * <p>
 * See <a href="https://github.com/Team766/FRC-Dashboard">Team766/FRC-Dashboard</a>
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
	private Reader in;
	private Writer out;
	
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
		StringBuilder inBuf = null;
		while (enabled) {
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
			try {
				int ch = in.read();
				if (ch == -1 || ch == ':') {
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
		// TODO
	}
	
	@Override
	public void step() {}
	
	@Override
	public void iterate() {}
	
}
