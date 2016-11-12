#!/usr/bin/python2.7

from os import system
import socket 
from socket import *
from select import select
import time

HOST = '192.168.0.21'
PORT = 12345
BUFSIZE = 1024
ADDR = (HOST,PORT)

clientSocket = socket(AF_INET, SOCK_STREAM)

system("ifconfig wlan0 | grep addr: &>test.out 2>&1")
f = open("./test.out", 'r')
ID = f.readline()
ID = str(ID[20:32])

system("ifconfig wlan0 | grep HWaddr &>test.out 2>&1")
f = open("./test.out", 'r')
BSSID = f.readline()
BSSID = str(BSSID[38:52])
CHECK = ID.count('192.')
if CHECK != 1:
	system("ifconfig br-lan | grep HWaddr &>test.out 2>&1")
	f = open("./test.out",'r')
	ID = f.readline()
	ID = str(ID[20:32])
	system("ifconfig br-lan | grep HWaddr &>test.out 2>&1")
	f = open("./test.out",'r')
	BSSID = f.readline()
	BSSID = str(BSSID[38:52])
print ID
print BSSID
system("uci get wireless.@wifi-iface[0].mode >test.out 2>&1")
f = open("./test.out",'r')
MODE = f.readline()
print MODE
CHECK = MODE.count('sta')오후 1:30 2016-10-11
if CHECK == 1: MODE = 'sta'
else: MODE = 'ap'

system("uci get wireless.@wifi-iface[0].ssid >test.out 2>&1")
f = open("./test.out", 'r')
SSID = f.readline()
print SSID

clientSocket = socket(AF_INET, SOCK_STREAM)
clientSocket.connect(ADDR)

print('[%s] [%s] [%s] [%s]' %(ID, MODE, SSID, BSSID))
clientSocket.sendall('[%s] [%s] [%s] [%s]' %(ID, MODE, SSID, BSSID))
