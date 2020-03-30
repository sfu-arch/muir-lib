

with open('log', 'r') as logfile:
	for line in logfile:
		if line.__contains__('LOG'):
			print(line)
#pvahdatn@cs-arch-20:~/git/dandelion-lib$ sbt "testOnly dataflow.test03Tester" > log
