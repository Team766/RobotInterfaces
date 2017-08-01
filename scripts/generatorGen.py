import sys


m_fd = open("output.txt", "w")

source = raw_input("File name with directory: ")
m_read = open(source, "r")

while True:
	line = m_read.readline()

	if not line: 
		break

	line = line.rstrip().replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t")

	m_fd.write("m_fd.write(\"" + line + "\\n\")\n")

m_fd.close()