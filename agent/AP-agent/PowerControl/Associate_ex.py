#!/usr/bin/python3

from os import system
import os

Best_AP_file = open("Best_AP","r")
Best_AP_line = Best_AP_file.readlines()

SSID = Best_AP_line[0][7:-1]
Addr = Best_AP_line[1][7:-1]
RSSI = Best_AP_line[2][7:-1]
CH   = Best_AP_line[3][7:-1]
#Erct = Best_AP_line[4][7:-1]

conf = "/etc/wpa_supplicant/wpa_supplicant.conf.%s" % SSID

# wpa_supplicant.conf file modify
with open("/etc/network/interfaces", "w") as interfaces:
        interfaces.write('auto lo\n\n')

        interfaces.write('iface lo inet loopback\n')
        interfaces.write('iface eth0 inet dhcp\n\n')

        interfaces.write('allow-hotplug wlan0\n')
        interfaces.write('auto wlan0\n')
        interfaces.write('iface wlan0 inet dhcp\n')
#       interfaces.write('wpa-roam ')##################
#       interfaces.write(conf)###############
        interfaces.write('wpa-conf /etc/wpa_supplicant/wpa_supplicant.conf')
        interfaces.write('\niface default inet dhcp\n\n')

#       interfaces.write('iface STA1 inet dhcp')

# wpa_supplicant.conf.SSID write
#with open(conf, "w") as wpa_conf:#############
with open("/etc/wpa_supplicant/wpa_supplicant.conf", "w") as wpa_conf:
        wpa_conf.write('ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev\n')
        wpa_conf.write('update_config=1\n\n')

        wpa_conf.write('network={\n')
        wpa_conf.write('        ssid=\"')
        wpa_conf.write(SSID)
        wpa_conf.write('\"\n')
        wpa_conf.write('        psk=\"79727990\"\n')    ####### Assume encrytion on and passwd fixed
        wpa_conf.write('}')
'''
# Encrytion set
if Erct == 'on' :
        with open(conf, "a") as wpa_conf:
                wpa_conf.write('        psk=\"79727990\"')      # Assume passwd fixed

# } closed
with open(conf,"a") as wpa_conf:
        wpa_conf.write('}')
'''
# Network restart
os.system("service networking reload")
