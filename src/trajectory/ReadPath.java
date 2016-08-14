package trajectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadPath {
	private Scanner reader;
	
	public Path getPath(String filePath) {
		System.out.println(filePath);
		try {
			reader = new Scanner(new File(this.getClass().getClassLoader().getResource(filePath).getPath()));
		} catch (FileNotFoundException e) {
			System.out.println("Error: Path file not opened");
		}
		System.out.println("Reading path from file...");

		String name = reader.nextLine();
		int num_elements = reader.nextInt();

		Trajectory left = new Trajectory(num_elements);
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

			left.setSegment(i, segment);
		}
		Trajectory right = new Trajectory(num_elements);
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

			right.setSegment(i, segment);
		}

		System.out.println("...finished reading file.");
		return new Path(name, new Trajectory.Pair(left, right));
	}
}
