package trajectory;

import interfaces.ConfigFileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadPath {
	private Scanner reader;
	
	public Path getPath(String filePath) {
		System.out.println(filePath);
		try {
			if(ConfigFileReader.onRobot)
				reader = new Scanner(new File("/home/lvuser/trajectories/" + filePath));
			else
				reader = new Scanner(this.getClass().getClassLoader().getResourceAsStream(filePath));
			
		} catch (FileNotFoundException | NullPointerException e) {
			System.out.println("Error: Path file not opened");
		}
		System.out.println("Reading path from file...");
		
		String name = reader.nextLine();
		int num_elements = reader.nextInt();
		
		Trajectory left = readTrajectory(num_elements);
		Trajectory right = readTrajectory(num_elements);
		
		System.out.println("...finished reading file.");
		return new Path(name, new Trajectory.Pair(left, right));
	}
	
	private Trajectory readTrajectory(int num_elements) {
		Trajectory trajectory = new Trajectory(num_elements);
		for (int i = 0; i < num_elements; i++) {
			Trajectory.Segment segment = new Trajectory.Segment();
			
			segment.pos = reader.nextDouble();
			segment.vel = reader.nextDouble();
			segment.acc = reader.nextDouble();
			segment.jerk = reader.nextDouble();
			segment.heading = reader.nextDouble();
			segment.dt = reader.nextDouble();
			segment.x = reader.nextDouble();
			segment.y = reader.nextDouble();
			
			trajectory.setSegment(i, segment);
		}
		return trajectory;
	}
}
