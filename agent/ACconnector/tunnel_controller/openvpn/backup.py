# -*- coding: utf-8 -*-

import os
import subprocess


DEVNULL = open(os.devnull, 'w')
PEER_SETTING_DIR = '/etc/ppp/peers/'


def find_active_pptp_connection():
    fd = subprocess.Popen("ps -ax | grep '/usr/sbin/pppd call'", shell=True, stdout=subprocess.PIPE)

    tunnel_dict = {}

    for line in fd.stdout.readlines():
        if 'grep' not in line:
            info_start = line.index('/')

            line = line[info_start:]
            line = line.replace('\n', '')
            pptp_info = line.split(' ')
            tunnel_name = pptp_info[2]

            tunnel_dict[tunnel_name] = {}

        else:
            continue

    return tunnel_dict


def find_setting(file_name):
    fp = open(PEER_SETTING_DIR + file_name, 'r')
    
    server_ip = fp.readline().split(' ')[2]
    username = fp.readline().split(' ')[1]

    return (server_ip, username)


def find_pptp_settings():
    setting_dict = {}

    setting_files = os.listdir(PEER_SETTING_DIR)

    setting_files.remove('provider')

    for setting_file in setting_files:
        setting_server_ip, setting_username = find_setting(setting_file)
        setting_dict[setting_file] = {'server_ip': setting_server_ip, 'username': setting_username}

    return setting_dict


def pptp_setting_mode(setting_dict):
    while True:
        print 
        print 'Host AP PPTP Tunnel Controller - Setting check mode'
        print '1. List current PPTP peer settings'
        print '2. Add new PPTP peer setting'
        print '3. Remove PPTP peer setting'
        print '4. Add new username-password setting'
        print '5. Return to main'

        try:
            print 'Select mode: ',
            selection = int(raw_input())

        except ValueError:
            print 'Enter proper number (1~5)'
            print 'Returning to program start'

        if selection == 1:
            setting_files = os.listdir('/etc/ppp/peers')
            setting_files.remove('provider')

            for setting_file in setting_files:
                setting_server_ip, setting_username = find_setting(setting_file)
                setting_dict[setting_file] = {'server_ip': setting_server_ip, 'username': setting_username}

            print
            for setting_name, setting_info in sorted(setting_dict.iteritems()):
                print setting_name, ': ', setting_info['server_ip'], ',', setting_info['username'] 


        elif selection == 2:
            file_content = ''

            print 'Connection name: ',
            connection_name = raw_input()

            print 'Enter Target IP: ',
            target_ip = raw_input()

            print 'Username: ',
            username = raw_input()

            file_content += ('pty "pptp ' + target_ip + ' --nolaunchpppd"\n')
            file_content += ('name ' + username + '\n')
            file_content += ('remotename PPTP\n')
            file_content += ('require-mppe-128\n')
            file_content += ('file /etc/ppp/options.pptp')

            option_file = open('/etc/ppp/peers/%s' % connection_name, 'w')
            option_file.write(file_content)

            print 'Successfully added new PPTP peer setting'

        elif selection == 3:
            print
            print 'Please enter connection name: ',
            pptp_setting_name = raw_input()

            pptp_setting_full_name = '/etc/ppp/peers/' + pptp_setting_name
            cmd_result = subprocess.call(['rm', pptp_setting_full_name], stdout=DEVNULL, stderr=DEVNULL)

            if cmd_result == 0:
                print 'Successfully deleted connection setting %s' % pptp_setting_name

            else:
                print 'This name of setting file (%s) does not exit' % pptp_setting_name

        elif selection == 4:
            user_setting_file = open('/etc/ppp/chap-secrets', 'r+')

            file_content = user_setting_file.readlines()[2:]
            file_content = map(lambda x: x.replace('\n', ''), file_content)

            print 'Enter username: ',
            username = raw_input()

            print 'Enter password: ',
            password = raw_input()

            file_str = '%s %s %s %s' % (username, 'PPTP', password, '*')

            file_content.append(file_str)
            file_content.append('')

            user_setting_file.write(file_str + '\n\n')

            print 'Added new username-password set'

        elif selection == 5:
            return


def pptp_connection_mode(tunnel_dict, setting_dict):
    while True:
        print 
        print 'Host AP PPTP Tunnel Controller - Connection management mode'
        print '1. List currently active PPTP connections'
        print '2. Activate new connection'
        print '3. Deactivate currently active connection'
        print '4. Check connection status'
        print '5. Return to main'

        try:
            print 'Select mode: ',
            selection = int(raw_input())

        except ValueError:
            print 'Need to enter proper number (1~4)'
            print 'Returning to program start'

        if selection == 1:
            active_connection_dict = find_active_pptp_connection()

            for tunnel_name in active_connection_dict.keys():
                active_connection_dict[tunnel_name] = setting_dict[tunnel_name]

            tunnel_dict = active_connection_dict

            for tunnel_name, tunnel_info in sorted(tunnel_dict.iteritems()):
                print tunnel_name, ': ', tunnel_info['server_ip'], ',', tunnel_info['username']

        elif selection == 2:
            print 'Please input tunnel name'
            tunnel_name = raw_input()

            if tunnel_name not in setting_dict:
                print 'Tunnel %s cannot be created' % tunnel_name

            else:
                subprocess.call(['pon', tunnel_name])
                print 'Tunnel %s have been created' % tunnel_name
            
        elif selection == 3:
            print 'Please input tunnel name'
            tunnel_name = raw_input()

            if tunnel_name not in tunnel_dict:
                print 'Tunnel %s is not active connection' % (tunnel_name)

            else:
                subprocess.call(['poff', tunnel_name])
                print 'Tunnel %s have been destroyed' % (tunnel_name)

        elif selection == 4:
            pass

        elif selection == 5:
            return


def tunnel_control():
    tunnel_dict = find_active_pptp_connection()
    setting_dict = find_pptp_settings()

    while True:
        print
        print 'Host AP PPTP Tunnel Controller'
        print '1. Check PPTP connection settings'
        print '2. Activate | Deactivate VPN connections'
        print '3. Quit'

        try:
            print 'Select mode: ',
            selection = int(raw_input())

        except ValueError:
            print 'Enter proper number (1~3)'
            continue

        if selection == 1:
            #os.system('ps -ax | grep /usr/sbin/pppd')
            pptp_setting_mode(setting_dict)
                    
        elif selection == 2:
            pptp_connection_mode(tunnel_dict, setting_dict)

        elif selection == 3:
            print
            print 'Qutting tunnel controller'
            return 

        else:
            print 'Enter proper number (1~3)'


if __name__ == '__main__':
    tunnel_control()
