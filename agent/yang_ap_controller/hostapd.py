"""

주석 부분 입력할거임.


"""

import os
import platform

'''
class ApWrapper:
    def __init__(self):
        self.hostapd = ''
        self.context = device_config_pb2.Devices.AccessPoint.Ap()

    def __getitem__(self, key):
        if not isinstance(key, int):
            return None
        return self.context.access_point_information.ap_list[key]

    def __setitem__(self, key, value):
        if not isinstance(key, int):
            return
        if not isinstance(value, tuple):
            return

        ap = self.context.access_point_information.ap_list[key]
        ap[value[0]] = value[1]

    def append(self, item):
        self.context.access_point_information.ap_list.extend([item])
'''


class hostapd:

    def get_ip_address(self, ifname):
        if self.plat == 'Linux':
            ip = os.popen('ip addr show eth0').read().split("inet ")[1].split("/")[0]
        else:
            ip = '127.0.0.1'
        return ip

    def __init__(self, ap_listener, path = "./openwinnet.conf"):

        self.path = path
        self.plat = platform.system()
        self.ap_listener = ap_listener

        self.is_starting = False
        self.version = -1

        ### mapping method
        self.method_map = {
            'ssid' : 'set_ssid',
            'ss' : 'set_ssid',
            'name' : 'set_ssid',
            'password' : 'set_password',
            'pwd' : 'set_password',
            'channel' : 'set_channel'
        }

        ### default configurable dictionary
        self.config_dict = {
            'ssid': 'pqoqoqoqmfodqmfo',
            'ip': '127.0.0.1',
            'channel' : '1',
            'hw_mode' : 'g',
            'power_on_off' : '0'
        }


        ### default static dictionary
        self.static_dict = {
            'wpa_passphrase': '12345678',
            'interface' : 'wlan0',
            'wpa' : '3',
            'wpa_key_mgmt' : 'WPA-PSK',
            'wpa_pairwise' : 'TKIP',
            'rsn_pairwise' : 'CCMP',
            'auth_algs' : '1',
            'driver' : 'nl80211',
            'ctrl_interface' : '/var/run/hostapd',
            'ctrl_interface_group' : '0',
            'ieee80211n' : '1',
            'wmm_enabled' : '1',
            'ht_capab' : '[HT20][SHORT-GI[20]'
        }


        self.read_config()
        self.config_dict['ip'] = self.get_ip_address('eth0')

        self.ap_listener_init()



    def ap_listener_init(self):
        self.ap_listener.on_ap_changed(
            self.config_dict
        )


    def _edit_config(self, command, value):
        import inspect

        if command in self.method_map:
            command = self.method_map[command]

        if hasattr(self, command):
            func = getattr(self, command)
            if callable(func):
                if len(inspect.getargspec(func).args) > 1:
                    func(value)
                    self.write_config()
                    if self.is_starting == True:
                        self.stop()
                        self.start()
                else:
                    func()



    #### setter
    def set_ssid(self, value):
        self.config_dict['ssid'] = value
        self.ap_listener.on_ap_changed(
            {'ssid': value }
        )

    def set_channel(self, value):
        self.config_dict['channel'] = value
        self.ap_listener.on_ap_changed(
            { 'channel' : value }
        )

    def set_mode(self, value):
        self.config_dict['hw_mode'] = value
        self.ap_listener.on_ap_changed(
            { 'hw_mode' : value }
        )

    def set_power_on_off(self, value):
        self.config_dict['power_on_off'] = value
        self.ap_listener.on_ap_changed(
            { 'power_on_off' : value }
        )


    def read_config(self):
        f = open('./openwinnet.conf', 'r')
        lines = f.readlines()
        for line in lines:
            key, val = line.split('=')
            val = val.replace('\n', '')
            if key in self.static_dict:
                if val != '' or len(val) != 0 or val is not None:
                    self.static_dict[key] = val
            elif key in self.config_dict:
                self.config_dict[key] = val
        f.close()


    def write_config(self):
        f = open('./openwinnet.conf', 'w')

        for key, val in self.config_dict.items():
            if key != 'ip' and key != 'power_on_off':
                f.write(key + '=' + val + '\n')
        for key, val in self.static_dict.items():
            f.write(key + '=' + val + '\n')
        f.close()



    def start(self):
        import subprocess

        if self.plat == 'Linux':
            print(subprocess.getoutput("nmcli radio wifi off"))
            print(subprocess.getoutput("rfkill unblock wlan"))
            print(subprocess.getoutput("ifconfig wlan0 192.168.1.34 up"))
            print(subprocess.getoutput("dhcpd"))
            print(subprocess.getoutput("hostapd -dd " + self.path + " -B"))

            '''
            subprocess.run(['nmcli', 'radio', 'wifi', 'off'])
            subprocess.run(['rfkill', 'unblock', 'wlan'])
            subprocess.run(['ifconfig', 'wlan0', '192.163.1.34', 'up'])
            subprocess.run(['dhcpd'])
            subprocess.run(['hostapd', '-dd', self.path, ' -B'])
            '''
            self.is_starting = True

        else:
            print ("We will develop other operating systems.")

    def stop(self):
        import subprocess
        if self.plat == 'Linux':
            '''
            subprocess.run(['skill', 'dhcpd'])
            subprocess.run(['skill', 'hostapd'])
            '''
            subprocess.getoutput("skill dhcpd")
            subprocess.getoutput("skill hostapd")
            self.is_starting = False

        else:
            print("We will develop other operating systems.")

  #  def stop_hostapd(self):
  #











