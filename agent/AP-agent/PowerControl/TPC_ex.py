#!/usr/bin/python3

#-*-coding: utf-8-*-

from os import system                   # import system function
import os                               # import os module

lan_off = 1                             # # of lan disconnected
os.system("sudo python ex_auto.py")                  # start hostapd

################ socket ################
os.system("netstat -ntlp")
import socket

soc = socket.socket()
host = "163.180.118.127"
port = 18118
soc.bind((host, port))
soc.listen(5)
########################################

while 1:
        ######## socket ########
        conn, addr = soc.accept()
        print ("Got connection from", addr)
        msg = conn.recv(1024)
        print(msg)

        ######## Power control ########
        system("mii-tool >link.out 2>&1")               # mii-tool save to link$
        system("ping -c 1 8.8.8.8 >ping.out 2>&1")      # ping save to ping.out

        link_file = open("./link.out","r")      # open link.out
        link_line = link_file.readlines()       # bring text file by list per l$
        link_state = link_line[0][-3:-1]        # slicing link_line 50~51(extra$
#       print(link_state)                       # ok / nk

        ping_file = open("./ping.out","r")      # open ping.out
        ping_all = ping_file.read()             # bring text file
        ping_state = ping_all.count('unreachable') # counting 'unreachable'
#       print(ping_state)                       # 0 / !0

        ######## socket's message -> raspi ########
        if msg == b'\x00':                      # echo stop
                '''
                if link_state != "ok":
                        lan_off = lan_off + 1
                        print("Disconnected!")
                        os.system("killall hostapd")
                elif ping_state != 0:
                        lan_off = lan_off + 1
                        print("Disconnected!")
                        os.system("killall hostapd")
                else:
                        print("Kill AP!")
                        os.system("killall hostapd")
                '''
                os.system("iwconfig wlan0 txpower 0")
                print("AP txpower = 0")

        elif msg == b'\x01':                    # echo start
                '''
                print("Connected!")
                if lan_off != 0:
#                       os.system("ifup wlan0")
                        os.system("./ex_auto")
                        lan_off = 0
                else:
                        pass
                '''
                os.system("iwconfig wlan0 txpower 10")
                print("AP txpower = 10")
        else:
                print("?????")
#       os.system("sleep 3")
