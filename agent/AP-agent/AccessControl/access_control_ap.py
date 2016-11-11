#!/usr/bin/python3

from os import system
import os

#socket
os.system("netstat -ntlp")
import socket
soc=socket.socket()
host="163.180.118.127"
port=8118
soc.bind((host,port))
soc.listen(5)

while 1:
	#socket
	conn, addr = soc.accept()
	msg=conn.recv(1024)
	print(msg)

	#mac_filter
	if msg == b'\x00':	#deny list X : service (O)
		os.system("killall hostapd")
		os.system("service hostapd restart")
		os.system("service dnsmasq restart")
		os.system("sleep 1")
		os.system("hostapd -B /home/mclab/OpenWinCon/OpenWinNet/agent/AP-agent/conf/openwinnet.conf")
		os.system("iptables -t nat -A POSTROUTING -j MASQUERADE")
		os.system('sh -c "echo 1 > /proc/sys/net/ipv4/ip_forward"')
	elif msg == b'\x01':	#accept list X : service (X)
		os.system("killall hostapd")
		os.system("service hostapd restart")
		os.system("service dnsmasq restart")
		os.system("sleep 1")
		os.system("hostapd -B /home/mclab/OpenWinCon/OpenWinNet/agent/AP-agent/conf/openwinnet_access.conf")
		os.system("sleep 2")
		os.system("iptables -t nat -A POSTROUTING -j MASQUERADE")
		os.system('sh -c "echo 1 > /proc/sys/net/ipv4/ip_forward"')
	else:
		print("error")
