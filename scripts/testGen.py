import sys


'''
	Script for generating simple tests for the robot

	Run with [test name, location] as the paramters, 
	or no paramters and fill in the prompts.

	@author Brett Levenson
'''

def generateFile(testName, directory):
	m_fd = open(directory + "/" + testName + ".java", "w")

	m_fd.write("package " + directory.replace("/", ".") + ";\n")
	m_fd.write("\n")
	m_fd.write("import org.junit.Test;\n")
	m_fd.write("import com.team766.lib.ConfigFile;\n")
	m_fd.write("\n")
	m_fd.write("import tests.*;\n")
	m_fd.write("\n")
	m_fd.write("/*\n")
	m_fd.write("\n")
	m_fd.write("\tTest for " + testName + " subsystem\n")
	m_fd.write("\n")
	m_fd.write("\tTests include:\n")
	m_fd.write("\t\tTest1 - tests the functionality of ...\n")
	m_fd.write("\n")
	m_fd.write("\t@author YOUR_NAME\n")
	m_fd.write("\n")
	m_fd.write("\tTODO: ADD THIS TEST TO AllTests.java\n")
	m_fd.write("\n")
	m_fd.write("*/\n")
	m_fd.write("\n")
	m_fd.write("public class " + testName + " extends TestCase {\n")
	m_fd.write("\n")
	m_fd.write("\t// Make sure you keep the @Test and have all test methods preceded by the word \"test\"\n")
	m_fd.write("\t@Test\n")
	m_fd.write("\tpublic void testSampleTest() throws Exception {\n")
	m_fd.write("\n")
	m_fd.write("\t\t// Send message you are testing\n")
	m_fd.write("\t\tScheduler.getInstance().sendMessage(new SampleMessage(\"Brett is my hero\"));\n")
	m_fd.write("\n")
	m_fd.write("\t\t// Chech an assertion with a wait time to see the robot's response to the message\n")
	m_fd.write("\t\tassertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() > 100;}, 2);\n")
	m_fd.write("\n")
	m_fd.write("\t\t// Change the hardware using the test classes as a wrapper\n")
	m_fd.write("\t\t((tests.Gyro)instance.getGyro(ConfigFile.getGyro())).setAngle(0);\n")
	m_fd.write("\n")
	m_fd.write("\t\t// Check that changing the robot's inputs resulted in the correct action taken...with anther assertion\n")
	m_fd.write("\t\tassertTrueTimed(() -> {return instance.getMotor(ConfigFile.getRightMotor()[0]).get() == 0;}, 2);\n")
	m_fd.write("\t}\n")
	m_fd.write("\n")
	m_fd.write("\t// More tests below that belong to this category of tests\n")
	m_fd.write("}\n")

	m_fd.close()

def main():
	testName = ""
	directory = ""

	# Ask user for parameters
	if(len(sys.argv) == 1):
		print("Welcome to the Test generator")
		testName = raw_input("Test name: ")
		directory = raw_input("Directory for the test: ")

	# Read parameters from terminal
	elif(len(sys.argv) == 3):
		testName = sys.argv[1]
		directory = sys.argv[2]

	# Yell at the use for not passing in the correct paramters
	else:
		sys.exit("Either pass in the Test's name and location, or no paramters")

	#Check if they named the Actor.java
	if(".java" in testName):
		sys.exit("Please just pass in the test's name, not a file name(remove .java)")

	#Check whether the directory was passed in with an unnessary closing slash ("/")
	if(directory.endswith('/')):
		directory = directory[:-1]

	# Make sure everything is correct before creating files
	print("Going to create Test: " + directory + "/" + testName + ".java")
	cont = raw_input("Proceed? (y/n)")
	if(cont.lower() != "y"):
		sys.exit("That's a shame, I was really looking forward to creating it...")

	# Ready to create file
	generateFile(testName, directory)

	print("done...it was succesful")


if __name__ == "__main__":
    main()