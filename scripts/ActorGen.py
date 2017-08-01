import sys


'''
	Script for generating Actor subsystems

	Run with [actor name, location] as the paramters, 
	or no paramters and fill in the prompts.

	@author Brett Levenson
'''

def generateFile(actorName, directory):
	m_fd = open(directory + "/" + actorName + ".java", "w")

	m_fd.write("package " + directory.replace("/", ".") + ";\n")
	m_fd.write("\n")
	m_fd.write("//Put imports here\n")
	m_fd.write("import lib.*;\n")
	m_fd.write("\n")
	m_fd.write("public class " +  actorName + " extends Actor {\n")
	m_fd.write("\n")
	m_fd.write("\t//Instantiate local instances of hardware here from the HardwareProvider instance\n")
	m_fd.write("\n")
	m_fd.write("\tprivate boolean commandFinished;\n")
	m_fd.write("\tprivate Message currentMessage;\n")
	m_fd.write("\tprivate SubActor currentCommand;\n")
	m_fd.write("\n")
	m_fd.write("\tpublic void init() {\n")
	m_fd.write("\t\t//Put all the messages this Actor will recieve and process here\n")
	m_fd.write("\t\tacceptableMessages = new Class[]{};\n")
	m_fd.write("\n")
	m_fd.write("\t\t//Instantiate variables below\n")
	m_fd.write("\t\tcommandFinished = false;\n")
	m_fd.write("\t}\n")
	m_fd.write("\n")
	m_fd.write("\tpublic void iterate(){\n")
	m_fd.write("\n")
	m_fd.write("\t\t//Check for new messages\n")
	m_fd.write("\t\tif(newMessage()){\n")
	m_fd.write("\t\t\tstopCurrentCommand();\n")
	m_fd.write("\t\t\tcommandFinished = false;\n")
	m_fd.write("\n")
	m_fd.write("\t\t\tcurrentMessage = readMessage();\n")
	m_fd.write("\n")
	m_fd.write("\t\t\tif(currentMessage == null)\n")
	m_fd.write("\t\t\t\treturn;\n")
	m_fd.write("\n")
	m_fd.write("\n")
	m_fd.write("\t\t\t//Cascading if statments to determine which message was recieved\n")
	m_fd.write("\t\t\t/*\n")
	m_fd.write("\t\t\tif(currentMessage instanceof Message1){\n")
	m_fd.write("\t\t\t\t//Do something\n")
	m_fd.write("\t\t\t}\n")
	m_fd.write("\t\t\telse if(currentMessage instanceof Message2)\n")
	m_fd.write("\t\t\t\tcurrentCommand = new Message2SubCommand(currentMessage);\n")
	m_fd.write("\t\t\telse if(currentMessage instanceof Message3)\n")
	m_fd.write("\t\t\t\tcurrentCommand = new Message3Command(currentMessage);\n")
	m_fd.write("\t\t\t*/\n")
	m_fd.write("\n")
	m_fd.write("\t\t}\n")
	m_fd.write("\n")
	m_fd.write("\t\tstep();\n")
	m_fd.write("\n")
	m_fd.write("\t\t//Send Status update message for this Actor\n")
	m_fd.write("\t\t//sendMessage(new StatusUpdate(commandFinished, currentMessage, other variables...);\n")
	m_fd.write("\n")
	m_fd.write("\t\t//Update local variables\n")
	m_fd.write("\t\t//updateVariableX();\n")
	m_fd.write("\n")
	m_fd.write("//\t\titsPerSec++;\n")
	m_fd.write("\t}\n")
	m_fd.write("\n")
	m_fd.write("\tpublic void run() {\n")
	m_fd.write("\t\twhile(enabled){\n")
	m_fd.write("\t\t\titerate();\n")
	m_fd.write("\t\t\tsleep();\n")
	m_fd.write("\t\t}\n")
	m_fd.write("\n")
	m_fd.write("\t\t//Stop all processes\n")
	m_fd.write("\t\tstopCurrentCommand();\n")
	m_fd.write("\t}\n")
	m_fd.write("\n")
	m_fd.write("\tpublic void step(){\n")
	m_fd.write("\t\tif(currentCommand != null){\n")
	m_fd.write("\t\t\tif(currentCommand.isDone()){\n")
	m_fd.write("\t\t\t\tstopCurrentCommand();\n")
	m_fd.write("\t\t\t}else{\n")
	m_fd.write("\t\t\t\tcurrentCommand.update();\n")
	m_fd.write("\t\t\t}\n")
	m_fd.write("\t\t}\n")
	m_fd.write("\t}\n")
	m_fd.write("\n")
	m_fd.write("\tprivate void stopCurrentCommand(){\n")
	m_fd.write("\t\tif(currentCommand != null){\n")
	m_fd.write("\t\t\tcurrentCommand.stop();\n")
	m_fd.write("\n")
	m_fd.write("\t\t\t//Send update message that current message has finished being processed\n")
	m_fd.write("\t\t\t//sendMessage(new StatusUpdate(true, currentMessage, other variables...));\n")
	m_fd.write("\t\t}\n")
	m_fd.write("\t\tcurrentCommand = null;\n")
	m_fd.write("\t\tcommandFinished = true;\n")
	m_fd.write("\t}\n")
	m_fd.write("\n")
	m_fd.write("\n")
	m_fd.write("\t// Methods to update local variables\n")
	m_fd.write("\n")
	m_fd.write("\t//Protected getter methods\n")
	m_fd.write("\n")
	m_fd.write("\t//Protected setter methods\n")
	m_fd.write("\n")
	m_fd.write("\t//Protected reset methods\n")
	m_fd.write("\n")
	m_fd.write("}\n")


	m_fd.close()

def main():
	actorName = ""
	directory = ""

	# Ask user for parameters
	if(len(sys.argv) == 1):
		print("Welcome to the Actor generator")
		actorName = raw_input("Actor name: ")
		directory = raw_input("Directory for the actor: ")

	# Read parameters from terminal
	elif(len(sys.argv) == 3):
		actorName = sys.argv[1]
		directory = sys.argv[2]

	# Yell at the use for not passing in the correct paramters
	else:
		sys.exit("Either pass in the Actor's name and location, or no paramters")

	#Check if they named the Actor.java
	if(".java" in actorName):
		sys.exit("Please just pass in the actor's name, not a file name(remove .java)")

	#Check whether the directory was passed in with an unnessary closing slash ("/")
	if(directory.endswith('/')):
		directory = directory[:-1]

	# Make sure everything is correct before creating files
	print("Going to create Actor: " + directory + "/" + actorName + ".java")
	cont = raw_input("Proceed? (y/n)")
	if(cont.lower() != "y"):
		sys.exit("That's a shame, I was really looking forward to creating it...")

	# Ready to create file
	generateFile(actorName, directory)

	print("done...it was succesful")


if __name__ == "__main__":
    main()