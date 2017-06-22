package interfaces;

import java.io.*;
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
	
	public static final String fileName = "";
	public static boolean onRobot = true;
	
	private HashMap<String, int[]> devices = new HashMap<>();
	private HashMap<String, String> strings = new HashMap<>();
	
	public static ConfigFileReader getInstance() {
		if (instance == null)
			instance = new ConfigFileReader();
		return instance;
	}
	
	private ConfigFileReader() {
		onRobot = !fileName.equals("config.txt") && !fileName.equals("simConfig.txt");
		
		System.out.println("Loading config file: " + fileName);
		try {
			Reader source = onRobot? new FileReader(fileName) : new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName));
			BufferedReader reader = new BufferedReader(source);
			
			String currLine = reader.readLine();
			while (currLine != null) {
				//Separate the key from the value
				int sepIndex = currLine.indexOf(",");
				String key = currLine.substring(0, currLine.indexOf(","));
				String value = currLine.substring(currLine.indexOf(",") + 1);
				if (wantAString(key))
					strings.put(key, value);
				else
					devices.put(key, stringsToInts(value.split(",")));
				currLine = reader.readLine();
			}
			reader.close();
			
		} catch (IOException | NullPointerException e) {
			System.err.println("Failed to load config file!\t:(");
			e.printStackTrace();
		}
	}
	
	private boolean wantAString(String key) {
		return key.equals("axisCamera") ||
		       key.equals("usbCamera");
	}
	
	private int[] stringsToInts(String[] in) {
		int[] out = new int[in.length];
		for (int i = 0; i < in.length; i++) {
			out[i] = Integer.parseInt(in[i]);
		}
		return out;
	}
	
	public int[] getPorts(String key) {
		return devices.get(key);
	}
	
	public int getPort(String key) {
		return getPorts(key)[0];
	}
	
	public String[] getString(String key) {
		return new String[]{key, strings.get(key)};
	}
	
}
