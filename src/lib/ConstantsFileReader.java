package lib;

import java.io.BufferedReader;
import java.io.FileReader;
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
	
	public static String fileName = "";
	public static boolean onRobot = true;
	
	private BufferedReader reader;
	private HashMap<String, Double> constants;
	private HashMap<String, String> constantStr;
	
	public static ConstantsFileReader getInstance(){
		if(instance == null)
			instance = new ConstantsFileReader();
		return instance;
	}
	
	public double get(String key){
		return constants.containsKey(key) ? constants.get(key) : 0.0;
	}
	
	public String getStr(String key){
		return constantStr.containsKey(key) ? constantStr.get(key) : "";
	}
	
	public ConstantsFileReader(){
		onRobot = fileName.equals("constants.csv") || !fileName.equals("simConstants.csv");
		
		System.out.println("Loading constants file: " + fileName);
		constants = new HashMap<String, Double>();
		constantStr = new HashMap<String, String>();
		
		loadConstants();
	}
	
	public void loadConstants(){
		String currLine = "";
		try {
			if(!onRobot)
				reader = new BufferedReader(new FileReader(this.getClass().getClassLoader().getResource(fileName).getPath()));
			else
				reader = new BufferedReader(new FileReader(fileName));
			
			currLine = reader.readLine();
			String[] tokens;
			while(currLine != null){
				//Separate the key from the value
				tokens = currLine.split(",");
				
				try{
					constants.put(tokens[0], Double.valueOf(tokens[1]));
				}catch(Exception e){
					constantStr.put(tokens[0], tokens[1]);
				}
				
				currLine = reader.readLine();
			}
			reader.close();

		} catch (Exception e) {
			System.err.println("Failed to load constants file!\t:(");
			e.printStackTrace();
		}
	}
	
}
