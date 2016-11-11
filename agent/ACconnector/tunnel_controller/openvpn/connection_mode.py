# -*- coding: utf-8 -*-

import os
import subprocess
import setting_mode as sm


DEVNULL = open(os.devnull, 'w')


def get_active_connections_dic():
    try:
        fp = open('/etc/openvpn/connection.settings', 'r')

    except IOError:
        fp = open('/etc/openvpn/connection.settings', 'w')
        fp.close()
        fp = open('/etc/openvpn/connection.settings', 'r')

    lines = list(map(lambda x: x.replace('\n', ''), fp.readlines()))
    
    connection_dic = {}
    for line in lines:
        info = line.split(',')
        connection_name, server_ip, status, dev_name = info[0], info[1], info[2], info[3]

        if status == 'True':
            connection_dic[connection_name] = {'server_ip': server_ip, 'status': status, 'dev_name': dev_name}

    return connection_dic


def print_active_connections():
    connections_dic = get_active_connections_dic()
    print_target_lst = ['Connection name', 'Server IP', 'Status', 'Dev name']
    col_width = max(17, max(len(name) for name in print_target_lst)) + 2

    print
    print ''.join(name.ljust(col_width) for name in print_target_lst)
    for k, v in connections_dic.iteritems():
        print ''.join(
                entity.ljust(col_width) for entity in [k, v['server_ip'], v['status'], v['dev_name']])
    print

def add_active_connection():
    settings_dic = sm.get_settings_dic()
    connections_dic = get_active_connections_dic()

    sm.print_settings()

    print 'Select connection to activate: ',
    setting_name = raw_input()
    
    if setting_name not in settings_dic:
        print '%s does not exist in settings folder' % setting_name

    elif setting_name in connections_dic:
        print '%s is active connection' % setting_name
        
    else:
        command = "sudo openvpn --config /etc/openvpn/settings/%s/client.conf &" % setting_name
        subprocess.call(command, shell=True, stdout=DEVNULL)
        
        connection_result = subprocess.Popen("ps -ax | grep '/etc/openvpn/settings/%s'" % setting_name, shell=True, stdout=subprocess.PIPE)

        connection_result = connection_result.stdout.readlines()

        if len(connection_result) == 0:
            print 'Failed to connect'
            print

        else:
            command = 'sudo iptables -A POSTROUTING -o %s -j MASQUERADE -t nat' % settings_dic[setting_name]['dev_name']
            subprocess.Popen(command, shell=True, stdout=DEVNULL)

            print 'Connection successful'
            print 
            settings_dic[setting_name]['status'] = True
            connections_dic[setting_name] = settings_dic[setting_name]

    write_active_connections_file(connections_dic)
    sm.write_settings_file(settings_dic)
        

def del_active_connection():
    connections_dic = get_active_connections_dic() 
    
    print_active_connections()

    print 'Select connection to remove: ',
    connection_name = raw_input()

    if connection_name not in connections_dic:
        print '%s connection is not active or does not exist in settings' % connection_name
        return

    result_process = subprocess.Popen("ps -ax | grep 'openvpn --config /etc/openvpn/settings/%s'" % connection_name, shell=True, stdout=subprocess.PIPE)

    lines = result_process.stdout.readlines()
    result_process_id = int(lines[1].strip().split(' ')[0])

    subprocess.Popen("kill -9 %d" % result_process_id, shell=True)
   
    connections_dic.pop('%s' % connection_name, None)
    
    write_active_connections_file(connections_dic)

    settings_dic = sm.get_settings_dic()
    settings_dic[connection_name]['status'] = False

    subprocess.Popen("sudo iptables -D POSTROUTING -o %s -j MASQUERADE -t nat" % settings_dic[connection_name]['dev_name'], shell=True, stdout=DEVNULL)

    sm.write_settings_file(settings_dic)


def write_active_connections_file(connections_dic):
    fp = open('/etc/openvpn/connection.settings', 'w')

    for k, v in connections_dic.iteritems():
        fp.write('%s,%s,%s,%s\n' % (k, v['server_ip'], v['status'], v['dev_name']))
    fp.close()


def check_connection():
    print_active_connections()

    connection_name = raw_input('Check which connection? ')
    settings_dic = sm.get_settings_dic()

    if connection_name not in settings_dic:
        print 'Connection does not exist'
        return 

    target_address = settings_dic[connection_name]['vpn_server_address']

    p = subprocess.Popen('ping -c1 -w5 -q %s > /dev/null; echo $?' % target_address,
            shell=True, stdout=subprocess.PIPE)
    result = int(p.communicate()[0])

    if result == 0:
        print
        print 'Connection is functional'
        print

    else:
        print
        print 'Connection is impaired'
        print
