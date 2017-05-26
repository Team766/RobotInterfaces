import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import sys
from Tkinter import * 
from ftplib import FTP
from StringIO import StringIO
import httplib
import time

# def onObjectClick(event):                  
#     print('Got object click', event.x, event.y)
#     print(event.widget.find_closest(event.x, event.y))

# root = Tk()
# canv = Canvas(root, width=500, height=500)
#obj2Id = canv.create_rectangle(50, 25, 150, 75, fill="green")

#canv.tag_bind(obj2Id, '<ButtonPress-1>', onObjectClick)       

HELP_OUTPUT = """
Sample Usage: python log_displayer.py SampleLog.txt

python log_displayer.py SampleLog.txt -n Drive -l FATAL

Flags:
	-f FTP file off the RoboRio: true/false
	-n Name of subsystems			
	-l Level
	-c Continous logging: Buffer size (-1 for display all)
	-m messages with certain subject
	-t time interval -t startTimeSec-endTimeSec inclusive
	-g graph values from messages with GRAPH X: val, Y: val using -g X,Y
	-g graph one value against time with GRAPH X: val using -g X
"""

START_TIME = 0; #ms
IP_ADDRESS = "roborio-766-frc.local"
SOCKET_PORT = 5800
FILE_NAME = "testLog.txt"
graphValuesX = []
graphValuesY = []
graph_values = []

mostRecentTimeStamp = 0;

bufferSize = 25

fig = plt.figure()
ax1 = fig.add_subplot(1, 1, 1)

conn = httplib.HTTPConnection(IP_ADDRESS, SOCKET_PORT)

def grab_HTTP_message(timeStamp):
	try:
		conn.request("GET", "/" + str(timeStamp), headers={"Connection":" keep-alive"})
		r1 = conn.getresponse()
		if(r1.status != "404"):
			return r1.read()
		return r1.status
	except httplib.BadStatusLine:
		conn.close()
		print "Failed to connect to server/robot :("
		return ""


def animate(i):
	global graphValuesX, graphValuesY, mostRecentTimeStamp

	message = grab_HTTP_message(mostRecentTimeStamp)

	if(message == ""):
		return

	messages = message.split("\n")
	outputMessages = []

	if(len(messages) < 1):
		return

	for i in range(0, len(messages)):
		messages[i] = messages[i].split()
		A = messages[i][:3]
		A.append(' '.join(messages[i][3:]))

		# Only add real messages, and ones that are going to be graphed
		if((A[0] == "404") or (A[0] == "null") or (not valueInArray(graph_values, A[3]))):
			continue

		outputMessages.append(A)

	if(len(outputMessages) < 1):
		return

	
	graphValuesX = []
	graphValuesY = []

	#Ensure graph values within buffer size
	# while((len(graphValuesX) > int(bufferSize)) and (len(graphValuesY) > int(bufferSize))):
	# 	graphValuesX.pop(0)
	# 	graphValuesY.pop(0)

	# print("BufferSize: ", bufferSize, " X: ", len(graphValuesX), " Y: ", len(graphValuesY), " T/F: ", (len(graphValuesX) > bufferSize))

	for mess in outputMessages:
		#Graphing one value vs time
		if(len(graph_values) == 1):
			graphValuesX.append(mess[1])
			graphValuesY.append(getValueFromString(graph_values[0], mess[3]))
		else:
			graphValuesX.append(getValueFromString(graph_values[0], mess[3]))
			graphValuesY.append(getValueFromString(graph_values[1], mess[3]))

	mostRecentTimeStamp = outputMessages[-1][1]

	#Update graph
	ax1.clear()
	# print zip(graphValuesX, graphValuesY)
	ax1.plot(graphValuesX, graphValuesY)
	fig.canvas.draw()

def valueInArray(a1, s1):
	for x in a1:
		if(x in s1):
			return True
	return False

#Checks that all the values in an array appear in a string
def allValsInArray(str, array):
	for word in array:
		if(not word in str):
			return False
	return True

def getAdjustedTime(time):
	return (int(time) - START_TIME) / 1000.0

#Adds val to every element in array
def shiftArrayByOffset(val, array):
	outArray = []
	for num in array:
		outArray.append(float(num) + val)
	return outArray

#str: X: val1 Y: val2
#val X
#returns val1
def getValueFromString(val, str):
	components = str.split(" ")
	for i in range(0, len(components) - 1):
		if(val in components[i]):
			return components[i + 1]
	return None

def main():
	if(len(sys.argv) < 2):
		sys.exit(HELP_OUTPUT)

	logLines = []

	global FILE_NAME
	FILE_NAME = sys.argv[1]

	#Check if grabbing file from ftp of robot or locally
	if("-f" in sys.argv):
		ftp = FTP('roborio-766-frc.local')
		ftp.login()
		ftp.cwd("/home/lvuser/")
		r = StringIO()
		ftp.retrbinary('RETR ' + FILE_NAME, r.write)
		inputfile = r.getvalue().split("\n")
		ftp.quit()
		r.close()

	else:
		inputfile = open(sys.argv[1])

	global START_TIME
	#Each line in logLines: [LEVEL, TIME, LOCATION, MESSAGE]
	for line in inputfile:
		lineSegment = line.split()
		#Group all the elements in the message section together
		A = lineSegment[:3]

		if(START_TIME == 0):
			START_TIME = float(A[1])

		A[1] = getAdjustedTime(A[1])
		A.append(' '.join(lineSegment[3:]))
		logLines.append(A)
		#logLines.append(lineSegment[:3].append(' '.join(lineSegment[3:])))

	global graphValuesX, graphValuesY, graph_values
	graphTimeOffset = 0

	subsystems = []
	display_levels = []
	message_strings = []
	timeInterval = [] # 0 index is start time in sec, 1 index is end time in sec

	#Load levels and subsystem name parameters into arrays
	for i in range(2, len(sys.argv) - 1, 2):
		if sys.argv[i] == "-n":
			subsystems.append(sys.argv[i+1])
		if sys.argv[i] == "-l":
			display_levels.append(sys.argv[i+1])
		if sys.argv[i] == "-m":
			message_strings.append(sys.argv[i+1])
		if sys.argv[i] == "-t":
			start_stop = sys.argv[i+1].split("-")
			timeInterval.append(float(start_stop[0]))
			timeInterval.append(float(start_stop[1]))
		if sys.argv[i] == "-g":
			graph_values = (sys.argv[i+1].split(","))


	#Graphics
	root = Tk()
	scrollbar = Scrollbar(root)
	scrollbar.pack( side = RIGHT, fill=Y )
	mylist = Listbox(root, background="black", yscrollcommand = scrollbar.set)
	mylist.config(width=0)

	#Add the lines to the display
	for line in range(0, len(logLines) - 1):
		#Filter messages by level and subsytem name
		if(len(subsystems) > 0):
			if(not valueInArray(subsystems, logLines[line][2])):
				continue
		if(len(display_levels) > 0):
			if(not valueInArray(display_levels, logLines[line][0])):
				continue
		if(len(message_strings) > 0):
			if(not valueInArray(message_strings, logLines[line][3])):
				continue
		if(len(timeInterval) > 0):
			if(logLines[line][1] <= timeInterval[0] or logLines[line][1] >= timeInterval[1]):
				continue
		if(len(graph_values) > 0):
			if(not valueInArray(graph_values, logLines[line][3])):
				continue
			else:
				#Proper Graph line: X: val Y: val
				#Or for just one line: X: val

				#Graphing one value vs time
				if(len(graph_values) == 1):
					graphValuesX.append(logLines[line][1])
					graphValuesY.append(getValueFromString(graph_values[0], logLines[line][3]))
				else:
					graphValuesX.append(getValueFromString(graph_values[0], logLines[line][3]))
					graphValuesY.append(getValueFromString(graph_values[1], logLines[line][3]))

		mylist.insert(END, logLines[line])

	   	messageType = logLines[line][0]
	   	if(messageType == "ERROR"):
	   		mylist.itemconfig(END, {'fg': 'red'})
	   	elif(messageType == "FATAL"):
	   		mylist.itemconfig(END, {'bg':'red'})
	   		mylist.itemconfig(END, {'fg':'white'})
	   	elif(messageType == "WARNING"):
	   		mylist.itemconfig(END, {'fg':'gold'})
	   	elif(messageType == "INFO"):
	   		mylist.itemconfig(END, {'fg':'lightskyblue'})
	   	elif(messageType == "DEBUG"):
	   		mylist.itemconfig(END, {'fg':'white'})
	   	else:
	   		mylist.itemconfig(END, {'fg':'black'})
	   		mylist.itemconfig(END, {'bg':'white'})

	#Remove Nones from the lists to be graphed
	graphValuesX = [x for x in graphValuesX if x is not None]
	graphValuesY = [y for y in graphValuesY if y is not None]

	mylist.pack(side = LEFT, fill = BOTH)
	scrollbar.config(command = mylist.yview)

	# print graphValuesX
	# print graphValuesY

	#Display graph, if applicable
	if(len(graph_values) > 0):
		if(len(graph_values) == 1):
			plt.xlabel("Time")
			plt.ylabel(graph_values[0])
			plt.plot(graphValuesX, graphValuesY)
		else:
			plt.xlabel(graph_values[0])
			plt.ylabel(graph_values[1])
			plt.plot(graphValuesX, graphValuesY, "o")

		if("-c" in sys.argv):
			ani = animation.FuncAnimation(fig, animate, interval=1000)
			global bufferSize
			bufferSize = sys.argv[sys.argv.index("-c") + 1]
		plt.show()

	root.mainloop()

if __name__ == "__main__":
    main()

'''
STARTING_X = 2
STARTING_Y = 2
WIDTH = 10
BLOCK_SIZE = 20
SPACING = 30

for y in range(STARTING_Y, STARTING_Y + WIDTH):
	y *= SPACING
	for x in range(STARTING_X, STARTING_X + WIDTH):
		x *= SPACING

		if((x/SPACING) % 2 == 1):
			canv.create_rectangle(x, y, x + BLOCK_SIZE, y + BLOCK_SIZE, fill="green")
		else:
			canv.create_rectangle(x, y, x + BLOCK_SIZE, y + BLOCK_SIZE, fill="red")

canv.pack()
root.mainloop()
'''