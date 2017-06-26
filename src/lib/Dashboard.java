package lib;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	/** The single {@code Dashboard} instance. */
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
			synchronized (this) {
				try {
					if (!isConnected()) {
						socket = serverSocket.accept();
						out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
						in = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
						inBuf = new StringBuilder();
					}
				} catch (IOException e) {
					log(Level.ERROR, "Dashboard server failed to accept a connection: " + e);
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						socket = null;
						out = null;
						in = null;
					}
					sleep();
					continue;
				}
			}
			
			// read a character from the socket
			try {
				int ch = in.read();
				if (ch == -1 || ch == ':') { // end of a token (EoF or colon)
					processToken(unescapeToken(inBuf.toString()));
					inBuf.setLength(0); // clear the buffer
				} else {
					inBuf.appendCodePoint(ch);
				}
			} catch (IOException e) {
				log(Level.ERROR, "Dashboard server failed to read data: " + e);
			}
		}
	}
	
	private static final Pattern unescapeRegex = Pattern.compile("%([01])");
	private static final char[] unescapeMap = {'%', ':'};
	
	private static String escapeToken(String token) {
		return token.replace("%", "%0").replace(":", "%1");
	}
	
	private static String unescapeToken(String token) {
		Matcher m = unescapeRegex.matcher(token);
		StringBuffer result = new StringBuffer(token.length());
		while (m.find()) {
			m.appendReplacement(result, "");
			result.append(unescapeMap[m.group(1).charAt(0) - '0']);
		}
		m.appendTail(result);
		return result.toString();
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
	
	//// public interface ////
	
	public synchronized boolean isConnected() {
		return socket != null && !socket.isClosed();
	}
	
	public void sendMessage(String type, Object... args) {
		sendMessage(type, Arrays.asList(args));
	}
	
	public void sendMessage(String type, List<Object> args) {
		StringBuilder msg = new StringBuilder(escapeToken(type) + ':' + args.size() + ':');
		for (Object arg : args) {
			msg.append(escapeToken(arg.toString())).append(':');
		}
		try {
			synchronized (this) {
				if (out == null) {
					log(Level.ERROR, "Failed to send message to dashboard: Not connected");
				} else {
					out.write(msg.toString());
				}
			}
		} catch (IOException e) {
			log(Level.ERROR, "Failed to send message to dashboard: " + e);
		}
	}
	
	@Override
	public void step() {}
	
	@Override
	public void iterate() {}
	
}
