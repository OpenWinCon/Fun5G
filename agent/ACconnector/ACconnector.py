# -*- coding: utf-8 -*-

from connect_module.AP_module.connector import *
#from tunnel_controller.HostAP.HostAP import *
import tunnel_controller.openvpn.AP as oap

def main():
    while True:
        print
        print 'Remote AP - SDN controller connector'
        print '1. Tunnel control mode'
        print '2. Connecting mode'
        print '0. Quit'

        try:
            print 'Select mode: ',
            selection = int(raw_input())

        except ValueError:
            print
            print 'Enter proper number'
            continue

        if selection == 1:
            oap.tunnel_control()
                    
        elif selection == 2:
            control_delegation()
                
        elif selection == 0:
            return


if __name__ == '__main__':
    main()
    print 'Quitting program'
