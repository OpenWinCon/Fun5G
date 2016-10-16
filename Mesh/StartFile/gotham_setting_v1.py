import random as np
import netifaces as ni
import os
import sys
import subprocess
import time

def check_MAJOR(major):
   if(major == "3840"):
      return True
   else:
      return False

def check_MINOR(minor):
   if(minor == "3840"):
      return True
   else:
      return False

def check_POWER(power):
   if(power == "-56"):
      return True
   else:
      return False

def check_mesh_beacon(scan_beacon):
   print("-----------check_mesh_beacon start----------")
   split_data = scan_beacon.split(' ')
   for i in split_data:
      checking_data=i.split(':')
      if(checking_data[0] == "MAJOR"):
         if(check_MAJOR(checking_data[1])==False):
            return False
      elif(checking_data[0] == "MINOR"):
         if(check_MINOR(checking_data[1])==False):
            return False
      elif(checking_data[0] == "POWER"):
         if(check_POWER(checking_data[1])==False):
            return False
      elif(checking_data[0] != "UUID"):
         return False

   print("chcking mesh : True")
   return True

def get_compatible_ip(ipSetting):
   print("----------get compatible ip----------")
   #ni.ifaddresses('br0')
   #ip = ni.ifaddresses('br0')[2][0]['addr']
   #ip_array = ip.split('.')

   ip_array = ipSetting.split('.')

   ip_dec = [0, 0, 0, 0]
   ip_hex = ['0', '0', '0', '0']
   for i in range(0, 4):
      ip_dec[i] = int(ip_array[i])
      ip_hex[i] = str(hex(ip_dec[i])[2:]).upper()

   beacon_compatible_IP = ip_hex[0] + " " + ip_hex[1] + " " + ip_hex[2] + " " + ip_hex[3]
   return beacon_compatible_IP

def get_information_from_line(line):
   split_data = line.split(' ')
   for i in split_data:
      checking_data=i.split(':')
      if(checking_data[0] == "UUID"):
         uuid = checking_data[1]
         break;
   version = uuid[0:2]
   masterIP = uuid[2:4] + " " + uuid[4:6] + " " + uuid[6:8] + " "+ uuid[8:10]
   ssid = uuid[10:12] + " " + uuid[12:14]
   channel = uuid[14:16]
   returnArray = [version, masterIP, ssid, channel]
   return returnArray

def searching_beacon():
   print("----------searching_beacon----------")
   os.system("hciconfig hci- down")
   time.sleep(2)
   os.system("hciconfig hci- up")
   time.sleep(2)
   print(">>>>>>>>>>beacon scanning<<<<<<<<<<")
   task = subprocess.Popen("timeout 3s ./beacon_scan_mesh.sh", shell=True, stdout=subprocess.PIPE)
   time.sleep(2)
   task = subprocess.Popen("timeout 10s ./beacon_scan_mesh.sh", shell=True, stdout=subprocess.PIPE)
   data = task.stdout.read()
   scan_beacon = data.split('\n')
   for i in scan_beacon:
      if(check_mesh_beacon(i)==True):
         return get_information_from_line(i)
   returnValue = ["False", "False"]
   return returnValue

def setting_beacon(version, ip, ssid, channel):
   print("----------setting_beacon & command----------")
   beacon_header = "1E 02 01 1A 1A FF "
   beacon_company = "00 FF "
   beacon_middle = "02 15 "

   mesh_version = version + " "
   mesh_masterIP = ip  + " "
   mesh_ssid=ssid + " "
   mesh_channel=channel + " "
   mesh_blank="00 00 00 00 00 00 00 00 "
   beacon_uuid = mesh_version + mesh_masterIP + mesh_ssid + mesh_channel + mesh_blank
   beacon_tail = "0F 00 0F 00 C8 00"
   print("version : %s" %mesh_version)
   print("masterIP : %s" %mesh_masterIP)
   print("ssid : %s" %mesh_ssid)
   print("channel : %s" %mesh_channel)

   #hci_name = sys.argv[1] + " "
   command = "sudo hcitool -i "
   command += "hci- "
   command += "cmd 0x08 0x008 " + beacon_header + beacon_company + beacon_middle + beacon_uuid + beacon_tail
   #print(command)
   os.system(command)
   return ssid + " " + channel

def first_random_setting(ipSetting):
   version = "00"
   masterIP = get_compatible_ip(ipSetting)
   ssid='{0:0>2}'.format(hex(np.randrange(0,256))[2:]).upper() + " " + '{0:0>2}'.format(hex(np.randrange(0,256))[2:]).upper()
   channel='{0:0>2}'.format(np.randrange(1,12))

   setting_value =[version, masterIP, ssid, channel]
   return setting_value

def setting_batman(isGWserver, ssid, channel):
   print("----------setting_batman----------")
   channel_str = str(channel)
   os.system("service network-manager stop")
   time.sleep(10)
   os.system("service network-manager start")
   time.sleep(10)
   os.system("service network-manager stop")
   time.sleep(10)

   os.system("ifconfig wlan0 down")
   os.system("iwconfig wlan0 mode ad-hoc essid " + ssid + " channel " + channel_str)
   os.system("ifconfig wlan0 up")
   os.system("modprobe batman-adv")
   os.system("batctl -m bat0 if add wlan0")
   os.system("ifconfig wlan0 mtu 1527")
   os.system("cat /sys/class/net/wlan0/batman_adv/iface_status")
   os.system("ifconfig bat0 up")

   time.sleep(5)

   if(isGWserver == "T"):
      os.system("batctl gw server")
   else:
      os.system("batctl gw client")

def setting_OVS(isGWserver, ipSetting):
   print("----------setting_OVS----------")
   os.system("ovs-vsctl del-br br0")
   os.system("ovs-vsctl add-br br0")
   os.system("ovs-vsctl add-port br0 bat0")
   os.system("ifconfig br0 up")

   if(isGWserver == "T"):
      os.system("ovs-vsctl add-port br0 eth0")
      os.system("ifconfig eth0 0")

   if(ipSetting == "F"):
      print("----------get ip from HDCP server----------")
      os.system("dhclient br0")
      time.sleep(10)
   else:
      os.system("ifconfig br0 " + ipSetting)

def setting_AP():
   task = subprocess.Popen("hostapd -dd /etc/hostapd/hostapd.conf", shell=True, stdout=subprocess.PIPE)
   time.sleep(5)

def setting_GOTHAM_main():
   os.system("java -jar GOTHAM_hazel.jar wlan0 1 192.168.10.10")

#[T or F] : Beacon setting or Default setting
#[T or F] : GW server or Not
#[T or F] : AP mode
#[T or F] : GOTHAM_main start
#[F or IP(193.168.10.10)] : DHCP or Static IP
if __name__ == "__main__":
   argvCount = len(sys.argv)
   if(argvCount != 6):
      print("----------check the parameter----------")
      sys.exit(0)
   startWithBeacon = sys.argv[1]
   isGWserver = sys.argv[2]
   isAP = sys.argv[3]
   isGOTHAM = sys.argv[4]
   ipSetting = sys.argv[5]

   if(startWithBeacon == "T"):
      print("----------start GOTHAM with beacon----------")
      beaconExisting=searching_beacon()
      print(beaconExisting)

      if(beaconExisting[0] == "False"):
         print("beacon is not detecting")
         first_setting = first_random_setting(ipSetting)
         ipSetting = "192.168.10.10"
      else:
         print("beacon detecting")
         first_setting = beaconExisting

      batman_info = setting_beacon(first_setting[0], first_setting[1], first_setting[2], first_setting[3])
      batman_info_split = batman_info.split(' ')
      conv_ssid = batman_info_split[0]+batman_info_split[1]
      setting_batman(isGWserver, conv_ssid, batman_info_split[2])
      setting_OVS(isGWserver, ipSetting)

   else:
      print("----------start GOTHAM without beacon----------")
      setting_batman(isGWserver, "gotham_public", 1)
      setting_OVS(isGWserver, ipSetting)

   if(isAP == "T"):
      print("----------start AP mode----------")
      setting_AP()

   if(isGOTHAM =="T"):
      print("----------start GOTHAM main----------")
      setting_GOTHAM_main()

