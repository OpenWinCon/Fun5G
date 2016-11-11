# -*- coding: utf-8 -*-

import os 
import subprocess

OPENVPN_FOLDER = '/etc/openvpn'


def add_setting():
    print
    print 'Setting Name: ',
    setting_name = raw_input()

    print 'Server IP: ',
    server_ip = raw_input()

    print 'Username: ',
    username = raw_input()

    print 'Specify device name: ',
    dev_name = raw_input()

    subprocess.call('mkdir /etc/openvpn/settings/%s' % setting_name, shell=True)
    subprocess.call('mkdir /etc/openvpn/settings/%s/keys' % setting_name, shell=True)

    subprocess.call('scp %s@%s:/etc/openvpn/client_keys.tar /etc/openvpn/settings/%s' % (username, server_ip, setting_name), shell=True)

    subprocess.call('tar -xvf /etc/openvpn/settings/%s/client_keys.tar -C /etc/openvpn/settings/%s/keys' % (setting_name, setting_name), shell=True)

    subprocess.call('cp /etc/openvpn/sample_client.conf /etc/openvpn/settings/%s/client.conf' % setting_name, shell=True)

    fp = open('/etc/openvpn/settings/%s/client.conf' % setting_name, 'r')

    lines = list(map(lambda x: x.replace('\n', ''), fp.readlines()))

    file_new_content = []
    for line in lines:
        if line.startswith('ca'):
            line = 'ca /etc/openvpn/settings/%s/keys/ca.crt' % setting_name

        elif line.startswith('cert'):
            line = 'cert /etc/openvpn/settings/%s/keys/client.crt' % setting_name

        elif line.startswith('key'):
            line = 'key /etc/openvpn/settings/%s/keys/client.key' % setting_name

        elif line.startswith('dev'):
            line = 'dev %s\n' % dev_name
            line += 'dev-type tun'

        elif line.startswith('ifconfig'):
            vpn_server_addr = line.split(' ')[2]

        file_new_content.append(line)
        
    fp = open('/etc/openvpn/settings/%s/client.conf' % setting_name, 'w')

    for line in file_new_content:
        fp.write(line + '\n')

    settings_dic = get_settings_dic()
    settings_dic[setting_name] = {'server_ip': server_ip, 'status': 'False', 'dev_name': dev_name, 'vpn_server_address': vpn_server_addr}

    write_settings_file(settings_dic)
    

def del_setting():
    settings_dic = get_settings_dic()

    print_settings()

    print 'Select setting you want to delete: ',
    setting_name = raw_input()

    if setting_name not in settings_dic:
        print '%s does not exist in settings' % setting_name
        return

    settings_dic.pop(setting_name, None)
    
    write_settings_file(settings_dic)

    subprocess.call('rm -rf /etc/openvpn/settings/%s' % setting_name, shell=True)

    print 'Setting %s deleted\n' % setting_name


def get_settings_dic():
    try:
        if not os.path.isdir(os.path.join(OPENVPN_FOLDER, 'settings')):
            setting_folder = os.makedirs(os.path.join(OPENVPN_FOLDER, 'settings'))

        setting_file = open(os.path.join(OPENVPN_FOLDER, 'settings', 'server.settings'), 'r')

    except OSError:
        os.makedirs(os.path.join(OPENVPN_FOLDER, 'settings'))

    except IOError:
        setting_file = open(os.path.join(OPENVPN_FOLDER, 'settings', 'server.settings'), 'w')
        setting_file.close()
        return {}

    settings = {}
    lines = list(map(lambda x: x.replace('\n', ''), setting_file.readlines()))

    for line in lines:
        info = line.split(',')
        setting_name = info[0]
        server_ip = info[1]
        status = info[2]
        dev_name = info[3]
        vpn_server_addr = info[4]

        settings[setting_name] = {'server_ip': server_ip, 'status': status, 'dev_name': dev_name, 'vpn_server_address': vpn_server_addr}

    return settings


def print_settings():
    settings_dic = get_settings_dic()
    name_lst = ['Setting Name', 'Server IP', 'Connection Status', 'Dev name']
    col_width = max(len(name) for name in name_lst) + 2

    print
    print ''.join(entity.ljust(col_width) for entity in name_lst)
    for k, v in settings_dic.iteritems():
        print ''.join(
                entity.ljust(col_width) for entity in [k, v['server_ip'], v['status'], v['dev_name']])

    print


def write_settings_file(settings_dic):
    fp = open('/etc/openvpn/settings/server.settings', 'w')
    for k, v in sorted(settings_dic.iteritems()):
        fp.write('%s,%s,%s,%s,%s\n' % (k, v['server_ip'], v['status'], v['dev_name'], v['vpn_server_address']))
    fp.close()
