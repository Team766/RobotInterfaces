package interfaces;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

/**
 * Class for reading config files
 * 
 * Data is read from CSV files in the format:
 * 
 * leftDrive,1
 * rightDrive,100
 * axisCamera,10.7.66.11
 * usbCamera,cam0
 * 
 * @author Brett Levenson
 *
 */

public class ConfigFileReader {
	
	private static ConfigFileReader instance;
	
	public static String fileName = "";
	public static boolean onRobot = true;
	
	private BufferedReader reader;
	private HashMap<String, int[]> devices;
	private HashMap<String, String> strings;
	
	public static ConfigFileReader getInstance(){
		if(instance == null)
			instance = new ConfigFileReader();
		return instance;
	}
	
	public ConfigFileReader(){
		onRobot = fileName.equals("config.txt") ? false : true;
		
		System.out.println("Loading config file: " + fileName);
		devices = new HashMap<String, int[]>();
		strings = new HashMap<String, String>();
		String currLine = "";
		try {
			if(!onRobot)
				reader = new BufferedReader(new FileReader(this.getClass().getClassLoader().getResource(fileName).getPath()));
			else
				reader = new BufferedReader(new FileReader(fileName));
			
			currLine = reader.readLine();
			while(currLine != null){
				//Separate the key from the value
				if(wantAString(currLine.substring(0, currLine.indexOf(","))))
					strings.put(currLine.substring(0, currLine.indexOf(",")), currLine.substring(currLine.indexOf(",") + 1));
				else
					devices.put(currLine.substring(0, currLine.indexOf(",")), stringToInt(currLine.substring(currLine.indexOf(",") + 1).split(",")));
				currLine = reader.readLine();
			}
			reader.close();

		} catch (Exception e) {
			System.err.println("Failed to load config file!\t:(");
			e.printStackTrace();
		}
	}
	
	private boolean wantAString(String key){
		return key.equals("axisCamera") ||
			   key.equals("usbCamera");
	}
	
	private int[] stringToInt(String[] in){
		int[] out = new int[in.length];
		
		for(int i = 0; i < in.length; i++){
			out[i] = Integer.parseInt(in[i]);
		}
		return out;
	}
	
	public int[] getPorts(String key){
		return devices.get(key);
	}
	
	public int getPort(String key){
		return getPorts(key)[0];
	}
	
	public String[] getString(String key){
		return new String[]{key, strings.get(key)};
	}
	
}
