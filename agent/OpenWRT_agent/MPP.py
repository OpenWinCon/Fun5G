#!/usr/bin/python2.7

from socket import *
from select import *
import sys
from time import ctime

HOST = '192.168.0.21'
PORT = 12345
BUFSIZE = 1024
ADDR (HOST,PORT)

serverSocket = socket(AF_INET, SOCK_STREAM)

serverSocket.bind(ADDR)

serverSocket.listen(10)
connection_list = [serverSocket]
print('start %s port' % str(PORT))

f = open("STA_list",'w')
f.close
f = open("AP_list:,'w')
f.close

while connection_list:
	try:
		print('wait')
		read_socket, write_socket, error_socket = select(connection_list, [], [], 10)
		for sock in read_socket:
			if sock == serverSocket:
				clientSocket, addr_info = serverSocket.accept()
				connection_list.append(clientSocket)
				print('[INFO] [%s]connection establishment[%s]' %(ctime(), addr_info[0]))
			else:
				data = sock.recv(BUFSIZE)
				if data:
					print('[INFO] [%s] Received data' % ctime())
					CHECK = data.count('sta')
					if CHECK == 1:
						f = open("STA_list",'a')
						f.write(data)
						f.close
						print data
				else:
					connection_list.remove(sock)
					sock.close()
					print('[INFO] [%s] disconnection' % ctime())
	except KeyboardInterrupt:
		serverSocket.close()
		sys.exit()

