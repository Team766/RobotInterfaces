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
	
	private String fileName = "";
	
	private BufferedReader reader;
	private HashMap<String, Integer> devices;
	
	public static ConfigFileReader getInstance(){
		if(instance == null)
			instance = new ConfigFileReader();
		return instance;
	}
	
	public ConfigFileReader(){
		System.out.println("Loading config file: " + fileName);
		devices = new HashMap<String, Integer>();
		String currLine = "";
		try {
			reader = new BufferedReader(new FileReader(fileName));
			
			currLine = reader.readLine();
			while(currLine != null){
				//Separate the key from the value
				devices.put(currLine.substring(0, currLine.indexOf(",")), Integer.parseInt(currLine.substring(currLine.indexOf(",") + 1)));
				currLine = reader.readLine();
			}
			
			reader.close();
		} catch (Exception e) {
			System.err.println("Failed to load config file!\t:(");
			e.printStackTrace();
		}
	}
	
	public int getPort(String key){
		return devices.get(key);
	}
	
	public void setFileName(String name){
		fileName = name;
	}
}
