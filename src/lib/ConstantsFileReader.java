package lib;

import java.io.*;
import java.util.HashMap;

/**
 * Class for loading in constants from the Constants file.
 * Constants that need to be tuned / changed
 * Returns zero if constant not found.
 * 
 * Data is read from CSV files in the format:
 * 
 * kDriveDistance, 100
 * kPDrive,1
 * kIDrive,0
 * 
 * @author Brett Levenson
 *
 */

public class ConstantsFileReader {
	
	private static ConstantsFileReader instance;
	
	public static ConstantsFileReader getInstance() {
		if (instance == null)
			instance = new ConstantsFileReader();
		return instance;
	}
	
	public static final String fileName = "";
	public static boolean onRobot = true;
	
	private HashMap<String, Double> constants = new HashMap<>();
	private HashMap<String, String> constantStr = new HashMap<>();
	
	public double get(String key) {
		return constants.getOrDefault(key, 0.0);
	}
	
	public String getStr(String key) {
		return constantStr.getOrDefault(key, "");
	}
	
	private ConstantsFileReader() {
		onRobot = fileName.equals("constants.csv") || !fileName.equals("simConstants.csv");
		
		System.out.println("Loading constants file: " + fileName);
		
		loadConstants();
	}
	
	public void loadConstants(){
		try {
			Reader source = onRobot? new FileReader(fileName) : new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName));
			BufferedReader reader = new BufferedReader(source);
			
			String currLine = reader.readLine();
			while (currLine != null) {
				//Separate the key from the value
				String[] tokens = currLine.split(",");
				
				try {
					constants.put(tokens[0], Double.valueOf(tokens[1]));
				} catch(NumberFormatException e) {
					constantStr.put(tokens[0], tokens[1]);
				}
				
				currLine = reader.readLine();
			}
			reader.close();
			
		} catch (IOException | NullPointerException e) {
			System.err.println("Failed to load constants file!\t:(");
			e.printStackTrace();
		}
	}
	
}
