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
 * also provided for sending messages from the robot to the dashboard.
 * <p>
 * This class is a singleton. Access its instance via {@link #getInstance()
 * Dashboard.getInstance()}.
 * <p>
 * See <a href="https://github.com/Team766/FRC-Dashboard">Team766/FRC-Dashboard on GitHub</a>
 *
 * @author Quinn Tucker
 */
public class Dashboard extends Actor {
	
	/** the single {@code Dashboard} instance */
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
	
	
	/** the port on which the TCP server listens */
	private static final int PORT = 5801;
	
	/** the {@code ServerSocket} that accepts the dashboard's TCP connection */
	private ServerSocket serverSocket;
	/** the {@code Socket} that represents the TCP connection to the dashboard */
	private Socket socket;
	/** a {@code Writer} for sending text data over the socket's {@code OutputStream} */
	private Writer out;
	/** a {@code Reader} for reading text data from the socket's {@code InputStream} */
	private Reader in;
	
	/// these are used for piecing together incoming messages ///
	/** a buffer to store the current token */
	private StringBuilder inBuf;
	/** the type of message received */
	private String msgType;
	/** the number of arguments to be received */
	private int msgLen = -1;
	/** the list of arguments received */
	private List<String> msgArgs;
	
	/**
	 * Creates {@linkplain #serverSocket the <code>ServerSocket</code>}.
	 * Does not accept a connection; that's handled by {@link #run()}.
	 */
	@Override
	public void init() {
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Repeatedly, while enabled:
	 * <ol><li>
	 *     Accepts a connection on {@linkplain #serverSocket the
	 *     <code>ServerSocket</code>}, if not already connected.
	 * </li><li>
	 *     Reads a character from {@linkplain #socket the <code>Socket</code>}
	 *     and buffers it, calling {@link #processToken(String)} when a
	 *     token delimiter ({@code ':'}) or EOF is encountered.
	 * </li></ol>
	 * Note that this method uses blocking I/O calls, so it might not
	 * immediately terminate when {@link #enabled} is set to {@code false}.
	 */
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
					sleep(); // sleep, so we don't spam errors too fast in case it fails repeatedly
					continue;
				}
			}
			
			try {
				int ch = in.read(); // read a character from the socket
				if (ch == -1 || ch == ':') { // end of a token (EOF or colon)
					processToken(unescapeToken(inBuf.toString()));
					inBuf.setLength(0); // clear the buffer
				} else {
					inBuf.appendCodePoint(ch); // add the character to the buffer
				}
			} catch (IOException e) {
				log(Level.ERROR, "Dashboard server failed to read data: " + e);
			}
		}
	}
	
	/** a compiled regular expression {@code Pattern} for replacing escape sequences */
	private static final Pattern unescapeRegex = Pattern.compile("%([01])");
	/** an array that maps an escape sequence index to its corresponding unescaped character */
	private static final char[] unescapeMap = {'%', ':'};
	
	/**
	 * Escapes the given token, returning a string that does not contain
	 * colons. This is done by replacing {@code "%"} with {@code "%0"}
	 * and {@code ":"} with {@code "%1"}.
	 *
	 * @param token the token to be escaped
	 * @return the resulting escaped string
	 */
	private static String escapeToken(String token) {
		return token.replace("%", "%0").replace(":", "%1");
	}
	
	/**
	 * Unescapes the given token, yielding the original string that may
	 * or may not contain colons. This is done by replacing {@code "%0"}
	 * with {@code "%"} and {@code "%1"} with {@code ":"}, in one operation.
	 *
	 * @param token the escaped token string
	 * @return the original, unescaped token
	 */
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
	
	/**
	 * This method is responsible for assembling complete messages (type
	 * and argument list) from the stream of individual tokens. Each
	 * colon-separated token read in {@link #run()} is unescaped and then
	 * passed to this method. If the token passed is the last token in a
	 * message (as determined from the number of expected arguments),
	 * a {@link DashboardMessage} is generated and sent to the {@link Scheduler}
	 * for other actors to receive.
	 *
	 * @param token the token to be processed
	 */
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
	
	/**
	 * Returns true if the robot is currently connected to the custom dashboard over TCP.
	 */
	public synchronized boolean isConnected() {
		return socket != null && !socket.isClosed();
	}
	
	/**
	 * @see #sendMessage(String, List)
	 */
	public boolean sendMessage(String type, Object... args) {
		return sendMessage(type, Arrays.asList(args));
	}
	
	/**
	 * Escapes, formats, and sends a message to the dashboard over TCP.
	 * This method is threadsafe.
	 *
	 * @param type the type of the message
	 * @param args the list of arguments (each argument is converted to a {@code String}
	 *             via the object's {@link Object#toString() toString()} method)
	 * @return {@code true} if the message was successfully sent; {@code false} if the
	 *         robot is not connected to the dashboard or if any I/O error occurred
	 */
	public boolean sendMessage(String type, List<Object> args) {
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
					return true;
				}
			}
		} catch (IOException e) {
			log(Level.ERROR, "Failed to send message to dashboard: " + e);
		}
		return false;
	}
	
	
	/** Unused. */
	@Override
	public void step() {}
	
	/** Unused. */
	@Override
	public void iterate() {}
	
}
