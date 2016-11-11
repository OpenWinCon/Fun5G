# -*- coding: utf-8 -*-

import subprocess

from interface_module import *
from connection_module import *


def controller_module():
    cmd = 'iptables -A FORWARD -d 8.8.8.8 -j NFQUEUE --queue-num 1'
    p1 = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE)

    while True:
        print 'Remote AP - Controller connector (Controller module)'
        print '1. List current connection'
        print '2. Packet filtering mode'
        print '3. Program quit'

        try:
            print 'Select mode: ',
            selection = int(raw_input())

        except ValueError:
            print 'Enter proper number (1~3)'
            continue

        if selection == 1:
            ifs = all_interfaces()
        
            print
            for if_name, if_addr in ifs:
                if 'ppp' in if_name:
                    print if_name, format_ip(if_addr)
            print

        elif selection == 2:
            packet_filter()
                
        elif selection == 3:
            cmd = 'iptables -D OUTPUT 1'
            subprocess.Popen(cmd.split(), stdout=subprocess.PIPE)
            return


if __name__ == '__main__':
    controller_module()
