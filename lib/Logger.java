package lib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Logger extends Actor {

	public enum Level{
		DEBUG, INFO, WARNING, ERROR, FATAL
	};

	private PrintWriter pw;
	private long startTime = 0;
	private boolean INDENT = false;
	private String name;
	
	private boolean htmlOnly;
	
	private String LogFolder = getLogFolderName();

	private String headerHtml = "<head><meta http-equiv=\"refresh\" content=\"1\"></head><body style=\"background-color:rgba(180, 28, 28, 0.8)\">";
	private static final int HTML_HISTORY_LENGTH = 200;
	private CircularBuffer htmlList = new CircularBuffer(HTML_HISTORY_LENGTH);
	private ArrayList<String> diskList = new ArrayList<String>();

	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public Logger(String fileName) {
		name = fileName;
		
		headerHtml += "<h2 style = \"color: white\">" + name + "</h2>  <p style = \"color: #fc4\">";
		htmlOnly = false;
		
		try {
			System.out.println(new File("/media/sda1/" + LogFolder).mkdir());
			pw = new PrintWriter(new FileWriter("/media/sda1/" + LogFolder + "/" + name + ".txt"));
		} catch (IOException e) {
			htmlOnly = true;
			System.out.println(e.getMessage());
			System.out.println("Something went wrong in the log's constructor");
		}
		startTime = System.currentTimeMillis() / 1000l;
	}

	public synchronized void log(Level level, String message) {
		String spacing = INDENT ? "\t\t" : "\t";
		htmlList.add(getHtmlTime() + spacing + message + "<br>");
		if(!htmlOnly){
			diskList.add(getTime() + spacing + message);
			if(level.compareTo(Level.ERROR) >= 0){
				flushDisk();
			}
		}
	}
	
	public synchronized void flushDisk() {
		if (pw == null) {
			return;
		}
		for (String message : diskList) {
			pw.println(message);
		}
		diskList.clear();
	}

	public synchronized void closeFile() {
		if (pw == null) {
			return;
		}
		flushDisk();
		pw.close();
	}

	private String getTime() {
		int totalSeconds = (int) ((System.currentTimeMillis()/1000l) - startTime);
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;
		return hours + ":" + minutes + ":" + seconds;
	}
	
	private String getHtmlTime(){
		return "<p1 style = \"color: white\">" + getTime() + "</p1>";
	}

	public boolean isIndent() {
		return INDENT;
	}

	public void setIndent(boolean indent) {
		INDENT = indent;
	}

	public String getName() {
		return name;
	}

	public synchronized String getHTML() {
		return headerHtml + String.join("", htmlList);
	}
	
	public synchronized void clearHTML(){
		log(Level.INFO, "Sucessfully cleared HTML log");
	}
	
	private String getLogFolderName(){
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
	}

	@Override
	public String toString() {
		return "Logger-" + name;
	}

	@Override
	public void iterate() {
		if (!htmlOnly) {
			try {
				scheduler.schedule(this::flushDisk, 0, TimeUnit.SECONDS);
			} catch (Exception ex) {
				log(Level.ERROR, "Could not schedule disk writing task: " + ex);
			}
		}
	}
}
