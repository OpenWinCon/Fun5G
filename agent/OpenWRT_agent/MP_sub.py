#!/usr/bin/python2.7

from os import system 
import os 
f = open("topology.out", 'w')
f.close

system("iw dev wlan0 scan | grep -e wlan0 -e SSID -e signal > topology.out 2>&1")
system("/etc/config/time") 
