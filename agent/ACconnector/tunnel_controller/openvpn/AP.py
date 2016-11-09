# -*- coding: utf-8 -*-


import os
import connection_mode as cm
import setting_mode as sm


OPENVPN_FOLDER = '/etc/openvpn'


def tunnel_control():
    while True:
        print
        print 'Host AP Tunnel Controller'
        print '1. Setting mode'
        print '2. Connection mode'
        print '0. Quit'

        try:
            print 'Select mode: ',
            selection = int(raw_input())

        except ValueError:
            print
            print 'Enter proper number'
            continue

        if selection == 1:
            setting_mode() 
                    
        elif selection == 2:
            connection_mode() 

        elif selection == 0:
            print
            print 'Qutting tunnel controller'
            print
            return 

        else:
            print
            print 'Enter proper number'
            print



def setting_mode():
    while True:
        print
        print 'Host AP Tunnel Controller (OpenVPN) - Setting'
        print '1. List current OpenVPN settings'
        print '2. Add new OpenVPN server'
        print '3. Remove OpenVPN server'
        print '0. Quit'
        
        try:
            print 'Select mode: ',
            selection = int(raw_input())

        except ValueError:
            print
            print 'Enter proper number'
            print
            continue
            
        if selection == 1:
            settings = sm.print_settings()

        elif selection == 2:
            sm.add_setting()

        elif selection == 3:
            sm.del_setting()

        elif selection == 0:
            return
        
        else:
            print
            print 'Enter proper number'
            print

def connection_mode():
    while True:
        print
        print 'Host AP Tunnel Controller (OpenVPN) - Connection'
        print '1. List current active OpenVPN connections'
        print '2. Activate connection'
        print '3. Deactive connection'
        print '4. Check connection status'
        print '0. Quit'

        try:
            print 'Select mode: ',
            selection = int(raw_input())

        except ValueError:
            print 'Enter proper number'
            print
            continue

        if selection == 1:
            cm.print_active_connections()
            
        elif selection == 2:
            cm.add_active_connection()
        
        elif selection == 3:
            cm.del_active_connection()

        elif selection == 4:
            cm.check_connection()

        elif selection == 0:
            return

        else:
            print 'Enter proper number'
            print


if __name__ == '__main__':
    tunnel_control()
#setting_mode()
#connection_mode()
