from Tkinter import * 
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
"""

def valueInArray(a1, s1):
	for x in a1:
		if(x in s1):
			return True
	return False


def main():
	if(len(sys.argv) < 2):
		sys.exit(HELP_OUTPUT)

	subsystems = []
	display_levels = []
	#Load levels and subsystem name parameters into arrays
	for i in range(2, len(sys.argv) - 1, 2):
		if sys.argv[i] == "-n":
			subsystems.append(sys.argv[i+1])
		if sys.argv[i] == "-l":
			display_levels.append(sys.argv[i+1])

	logLines = []
	inputfile = open(sys.argv[1])
	#Each line in logLines: [LEVEL, TIME, LOCATION, MESSAGE]
	for line in inputfile:
		lineSegment = line.split()
		A = lineSegment[:3]
		A.append(' '.join(lineSegment[3:]))
		logLines.append(A)
		#logLines.append(lineSegment[:3].append(' '.join(lineSegment[3:])))

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

	mylist.pack( side = LEFT, fill = BOTH )
	scrollbar.config( command = mylist.yview )

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