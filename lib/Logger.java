package lib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Logger {

	private PrintWriter pw;
	private long startTime = 0;
	private boolean INDENT = false;
	private String name;
	
	private HashMap<String, Integer> iterations = new HashMap<String, Integer>();
	
	private boolean htmlOnly;
	
	private String LogFolder = getLogFolderName();

	private String html = "<head><meta http-equiv=\"refresh\" content=\"1\"></head><body style=\"background-color:rgba(180, 28, 28, 0.8)\">";
	
	public Logger(String fileName) {
		name = fileName;
		
		html += "<h2 style = \"color: white\">" + name + "</h2>  <p style = \"color: #fc4\">";
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

	public void print(String message) {
//		if(htmlOnly){
//			if(INDENT)
//				html += getHtmlTime() + "\t\t" + message + "<br>";
//			else
//				html += getHtmlTime() + "\t\t" + message + "<br>";
//		}else{
//			try {
//				if (INDENT) {
//					html += getHtmlTime() + "\t\t" + message + "<br>";
//					pw.println(getTime() + "\t\t" + message);
//				} else {
//					html += getHtmlTime() + "\t\t" + message + "<br>";
//					pw.println(getTime() + "\t" + message);
//				}
//			} catch (NullPointerException e) {
//				System.out.println("Null Pointer alert!");
//			}
//		}
	}
	
	public void printPeriodic(String message, String key, int iters){
//		if(!iterations.containsKey(key)){
//			iterations.put(key, 0);
//			return;
//		}
//		
//		if(iterations.get(key) > iters){
//			print("-P " + message);
//			iterations.put(key, 0);
//		}else{
//			iterations.put(key, iterations.get(key) + 1);
//		}
	}

	public void printRaw(String in) {
//		if(htmlOnly)
//			html += in + "<br>";
//		else{
//			try {
//				html += in + "<br>";
//				pw.println(in);
//			} catch (NullPointerException e) {
//				System.out.println("Can't print raw value: " + in);
//			}
//		}
	}

	public void print(String message, int value) {
//		if(htmlOnly){
//			if(INDENT)
//				html += getHtmlTime() + "\t\t" + message + value + "<br>";
//			else
//				html += getHtmlTime() + "\t" + message + value + "<br>";
//		}else{
//			try {
//				if (INDENT) {
//					html += getHtmlTime() + "\t\t" + message + value + "<br>";
//					pw.println(getTime() + "\t\t" + message + value);
//				}
//	
//				else {
//					html += getHtmlTime() + "\t" + message + value + "<br>";
//					pw.println(getTime() + "\t" + message + value);
//				}
//			} catch (NullPointerException e) {
//				System.out.println("Can't save log!");
//			}
//		}
	}

	public void closeFile() {
//		try {
//			pw.close();
//		} catch (NullPointerException e) {
//			System.out.println("Can't save log!");
//		}
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

	public void setIndent(boolean iNDENT) {
		INDENT = iNDENT;
	}

	public String getName() {
		return name;
	}

	public String getHTML() {
		return html;
	}
	
	public void clearHTML(){
		html = "<head><meta http-equiv=\"refresh\" content=\"1\"></head><body style=\"background-color:rgba(180, 28, 28, 0.8)\">";
		html += "<h2 style = \"color: white\">" + name + "</h2>  <p style = \"color: #fc4\">";
		print("Sucessfully cleared HTML log");
	}
	
	@SuppressWarnings("deprecation")
	private String getLogFolderName(){
		Date date = new Date(System.currentTimeMillis());
		return new SimpleDateFormat("dd-MM-yyyy").format(date) + "_" + date.getHours() + "~" + date.getMinutes() + "~" + date.getSeconds();
	}
}
