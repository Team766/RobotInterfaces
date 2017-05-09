from Tkinter import * 
import matplotlib.pyplot as plt
import sys

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
	-n Name of subsystems			
	-l Level
	-m messages with certain subject
	-t time interval -t hh:mm:ss-hh:mm:ss
	-g graph values from messages with GRAPH X: val, Y: val using -g X,Y
	-g graph one value against time with GRAPH X: val using -g X
"""
START_TIME = 0;

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

def timeInSecs(time):
	#6:40:32 -> one value
	chunks = time.split(":")
	return (float(chunks[0]) * 3600.0) + (float(chunks[1]) * 60.0) + float(chunks[2])

def getAdjustedTime(time):
	return timeInSecs(time) - START_TIME

#Adds val to every element in array
def shiftArrayByOffset(val, array):
	outArray = []
	for num in array:
		outArray.append(float(num) + val)
	return outArray

def main():
	if(len(sys.argv) < 2):
		sys.exit(HELP_OUTPUT)

	logLines = []
	inputfile = open(sys.argv[1])
	#Each line in logLines: [LEVEL, TIME, LOCATION, MESSAGE]
	for line in inputfile:
		lineSegment = line.split()
		A = lineSegment[:3]
		A.append(' '.join(lineSegment[3:]))
		logLines.append(A)
		#logLines.append(lineSegment[:3].append(' '.join(lineSegment[3:])))

	START_TIME = float(timeInSecs(logLines[0][1]))

	graphValuesX = []
	graphValuesY = []
	graphTimeOffset = 0

	subsystems = []
	display_levels = []
	message_strings = []
	timeInterval = [] # 0 index is start time in sec, 1 index is end time in sec
	graph_values = []
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
			timeInterval.append(getAdjustedTime(start_stop[0]))
			timeInterval.append(getAdjustedTime(start_stop[1]))
		if sys.argv[i] == "-g":
			graph_values = (sys.argv[i+1].split(","))


	#Graphics
	root = Tk()
	scrollbar = Scrollbar(root)
	scrollbar.pack( side = RIGHT, fill=Y )
	mylist = Listbox(root, background="black", yscrollcommand = scrollbar.set)
	mylist.config(width=0)

	#Add the lines to the display
	for line in range(0,len(logLines)):
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
			if(getAdjustedTime(logLines[line][1]) < timeInterval[0] or getAdjustedTime(logLines[line][1]) > timeInterval[1]):
				continue
		if(len(graph_values) > 0):
			if((not "GRAPH" in logLines[line][3]) or (not allValsInArray(logLines[line][3], graph_values))):
				continue
			else:
				#Proper Graph line: GRAPH X: val Y: val
				#Or for just one line: GRAPH X: val

				#Graphing one value vs time
				if(len(graph_values) == 1):
					graphValuesX.append(getAdjustedTime(logLines[line][1]))
					graphValuesY.append(logLines[line][3].split(" ")[2])
				else:
					lineSplit = logLines[line][3].split(" ")[1:] # values without GRAPH
					graphValuesX.append(lineSplit[1])
					graphValuesY.append(lineSplit[3])


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

	mylist.pack(side = LEFT, fill = BOTH)
	scrollbar.config(command = mylist.yview)

	print graphValuesX

	#Display graph, if applicable
	if(len(graph_values) > 0):
		plt.plot(graphValuesX, graphValuesY)
		plt.xlabel(graph_values[0])
		if(len(graph_values) == 1):
			plt.ylabel("Time")
		else:
			plt.ylabel(graph_values[1])
		plt.show()
	
	mainloop()

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