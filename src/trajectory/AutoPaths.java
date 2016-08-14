package trajectory;

import java.util.Hashtable;

public class AutoPaths {	
	private static long startTime;
	
	// Make sure these match up!
	public static final int WALL_LANE_ID = 2;
	public final static String[] kPathNames = { "InsideLanePathFar",
												"CenterLanePathFar", 
												"WallLanePath", 
												"InsideLanePathClose",
												"StraightAheadPath" };
	public final static String[] kPathDescriptions = { "Inside, Far",
														"Middle Lane", 
														"Wall Lane",
														"Inside, Close", 
														"Straight ahead"};
	static Hashtable<String, Path> paths_ = new Hashtable<String, Path>();

	public static void loadPaths() {
		startTime = System.currentTimeMillis();
		ReadPath pathReader = new ReadPath();
		for (int i = 0; i < kPathNames.length; ++i) {
			Path path = pathReader.getPath(kPathNames[i]+ ".txt");
			paths_.put(kPathNames[i], path);
		}
		System.out.println("Parsing paths took: " + (System.currentTimeMillis() - startTime)/1000);
	}

	public static Path get(String name) {
		return paths_.get(name);
	}

	public static Path getByIndex(int index) {
		return paths_.get(kPathNames[index]);
	}
}