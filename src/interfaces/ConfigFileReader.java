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
 * 
 * @author Brett Levenson
 *
 */

public class ConfigFileReader {
	
	private static ConfigFileReader instance;
	
	public static String fileName = "";
	
	private BufferedReader reader;
	private HashMap<String, int[]> devices;
	
	public static ConfigFileReader getInstance(){
		if(instance == null)
			instance = new ConfigFileReader();
		return instance;
	}
	
	public ConfigFileReader(){
		System.out.println("Loading config file: " + fileName);
		devices = new HashMap<String, int[]>();
		String currLine = "";
		try {
			reader = new BufferedReader(new FileReader(fileName));
			
			currLine = reader.readLine();
			while(currLine != null){
				//Separate the key from the value
				devices.put(currLine.substring(0, currLine.indexOf(",")), stringToInt(currLine.substring(currLine.indexOf(",") + 1).split(",")));
				currLine = reader.readLine();
			}
			
			reader.close();
		} catch (Exception e) {
			System.err.println("Failed to load config file!\t:(");
			e.printStackTrace();
		}
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
	
}
