#!/usr/bin/python3

from os import system
import os

with open("Best_AP", 'w+') as Best_AP:
        Best_AP.write("SSID = ")

while 1:
        Handover = 0

        # scan list => list.out
#       system("iw wlan0 scan | egrep '^BSS|: channel|primary|signal|SSID|Encryption' >list.out 2>&1")
        system("iw wlan0 scan | egrep '^BSS|SSID|signal' >list.out 2>&1")
        list_file = open("list.out","r")
        list_line = list_file.readlines()

        # command failed
        if list_line == 'command failed: Device or resource busy (-16)':
                print (list_line)
                #pass
        else:
                AP_n = len(list_line) // 3      # Number of APs
                print (len(list_line), AP_n)

                # dictionary
                list = {}

                for i in range(0,AP_n-1):
#                       if list_line[i*4+3][0:2] != 'DS':
#                               i = i + 1

                        if i != 0:
                                # if current AP's quality higher then previous's
                                if int(list_line[int(i*3+1)][-11:-8]) > list['RSSI']:
                                        list['Addr'] = list_line[i*3][4:20]
#                                       list['Channel'] = int(list_line[i*4+3][26:-1])
                                        #list['Freq] = list_line[i*6+2][:]
                                        list['RSSI'] = int(list_line[i*3+1][-11:-8])
                                        #list['Encryption'] = list_line[i*6+4][35:-1]
                                        list['ESSID'] = list_line[i*3+2][7:-1]
                                ##### Need algorithm of related Channel #####
                        else:
                                list['Addr'] = list_line[0][4:20]
#                               list['Channel'] = int(list_line[3][26:-1])
                                #list['Freq'] = list_line[2][]
                                list['RSSI'] = int(list_line[1][-11:-8])
                                #list['Encryption'] = list_line[4][35:-1]
                                list['ESSID'] = list_line[2][7:-1]
#               end for

                print (list)

                Best_AP_file = open("Best_AP","r")
                Best_AP_line = Best_AP_file.readlines()
                # Previous Best AP is not equal Current Best AP
                if Best_AP_line[0][7:-1] != list['ESSID'] :
                        # Best_AP Write Best_AP's list 
                        with open("Best_AP", "w") as Best_AP:
                                ESSID = "SSID = %s\n" % list['ESSID']
                                Addr = "Addr = %s\n" % list['Addr']
                                RSSI = "RSSI = %d\n" % int(list['RSSI'])
#                               Channel = "CH.  = %d\n" % int(list['Channel'])
                                #Encryption = "Ecrt = %s\n" % list['Encryption']

                                Best_AP.write(ESSID)
                                Best_AP.write(Addr)
                                Best_AP.write(RSSI)
#                               Best_AP.write(Channel)
                                #Best_AP.write(Encryption)

                        # Association AP
                        os.system("./Associate_ex")     ###########################
                        print ("Change")
                # Previous Best AP is Current Best AP
                else:
                        print ("Not change")
                        #pass

                list_file.close()
#       end else
# end while
