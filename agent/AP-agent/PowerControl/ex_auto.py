#!/usr/bin/python3

from os import system
import os

os.system("sudo service hostapd restart")
os.system("sudo service dnsmasq restart")
os.system("sudo sleep 1")
os.system("sudo hostapd -B /home/mclab/OpenWinCon/OpenWinNet/agent/AP-agent/conf/openwinnet.conf")
os.system("sudo sleep 2")
os.system("sudo iptables -t nat -A POSTROUTING -j MASQUERADE")
os.system('sudo sh -c "echo 1 > /proc/sys/net/ipv4/ip_forward"')
