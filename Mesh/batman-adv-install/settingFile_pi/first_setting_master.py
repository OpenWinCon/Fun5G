import os

os.system("ifconfig wlan0 down")
os.system("iwconfig wlan0 mode ad-hoc essid junho-batman channel 8")
os.system("ifconfig wlan0 up")
os.system("modprobe batman-adv")
os.system("batctl if add wlan0")
os.system("ifconfig wlan0 mtu 1527")
os.system("cat /sys/class/net/wlan0/batman_adv/iface_status")
os.system("ifconfig wlan0 10.0.0.10")
os.system("ifconfig bat0 up")
os.system("ifconfig bat0 192.168.0.10")
os.system("batctl gw server")