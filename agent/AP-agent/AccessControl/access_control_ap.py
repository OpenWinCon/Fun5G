#!/usr/bin/python3

from os import system
import os

#socket
os.system("netstat -ntlp")
inport socket
skc=socket.socket()
host="115.145.145.151"
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
		os.system("hostapd -B /etc/hostapd/hostapd.conf.0")
		os.system("sleep 1")
		os.system("hostapd -B /etc/hostapd/hostapd.conf.0")
		os.system("sleep 2")
		os.system("iptables -t nat -A POSTROUTING -j MASQUERADE")
		os.system('sh -c "echo 1 > /proc/sys/net/ipv4/ip_forward"')
	elif msg == b'\x01':	#accept list X : service (X)
		os.system("killall hostapd")
		os.system("service hostapd restart")
		os.system("service dnsmasq restart")
		os.system("sleep 1")
		os.system("hostapd -B /etc/hostapd/hostapd.conf.1")
		os.system("sleep 1")
		os.system("hostapd -B /etc/hostapd/hostapd.conf.1")
		os.system("sleep 2")
		os.system("iptables -t nat -A POSTROUTING -j MASQUERADE")
		os.system('sh -c "echo 1 > /proc/sys/net/ipv4/ip_forward"')
	else:
		print("error")
