# -*- coding: utf-8 -*-

import time
import telnetlib

HOST = 'localhost'
PORT = 7505


class ServerOpenVPNManager():
    tn = telnetlib.Telnet(HOST, PORT)
    
    def __init__(self):
        self.tn.write('status\n')
        
        time.sleep(0.1)
        status_str_lst = self.tn.read_very_eager().split('\n')

        client_lst = []
        routing_lst = []

        client_start = False
        routing_start = False

        for line in status_str_lst:
            line = line.replace('\r', '')

            if line.startswith('Common'):
                client_start = True
                continue

            elif line.startswith('ROUTING'):
                client_start = False
                continue

            elif line.startswith('Virtual'):
                routing_start = True
                continue

            elif line.startswith('GLOBAL'):
                break

            if client_start:
                client_lst.append(line)
               
            if routing_start:
                routing_lst.append(line)

        if len(client_lst) == 0:
            self.clients_dic = {}
            return

        clients_dic = {}
        for client_str in client_lst:
            client_info = client_str.split(',')
            common_name, real_addr, bytes_rcvd, bytes_sent, start_time = client_info
            
            clients_dic[real_addr] = {'common_name': common_name, 'bytes_rcvd': bytes_rcvd, 'bytes_sent': bytes_sent, 'start_time': start_time}
        
        for routing_str in routing_lst:
            routing_info = routing_str.split(',')
            virt_addr, common_name, real_addr, last_ref = routing_info

            clients_dic[real_addr]['virt_addr'] = virt_addr

        self.clients_dic = clients_dic

    
    def get_clients_dic(self):
        return self.clients_dic


    def remove_client(self, client_addr):
        clients_addr = self.clients_dic.keys()

        for addr in clients_addr:
            if addr.startswith(client_addr):
                self.tn.write('kill %s' % addr)
                self.clients_dic.pop(addr, None)

                print '\n%s is now disconnected' % client_addr

                return


        print '\n%s is not a connected client\n' % client_addr

        return 


    def print_clients(self):
        col_names = ['Real addr', 'Virtual addr', 'Common name', 'Bytes rcvd', 'Bytes sent', 'Start time']
        col_width = 22 
        
        print '\n' + ''.join(name.ljust(col_width) for name in col_names)

        for k, v in self.clients_dic.iteritems():
            print ''.join(name.ljust(col_width) for name in [k, v['virt_addr'], v['common_name'], v['bytes_rcvd'], v['bytes_sent'], v['start_time']])


def main():
    server_manager = ServerOpenVPNManager()

    
    while True:
        print
        print 'Tunnel Controller for controller mode'
        print '1. Print connected clients list'
        print '2. Kill a connection (Can be reconnected)'
        print '0. Quit'

        try:
            print 'Select mode: ',
            selection = int(raw_input())

        except ValueError:
            print '\nEnter proper number\n'
            continue

        if selection == 1:
            server_manager.print_clients()
                    
        elif selection == 2:
            server_manager.print_clients()

            client_addr = raw_input('\nType the address of client you want to disconnect: ')

            server_manager.remove_client(client_addr)

        elif selection == 0:
            print
            print 'Qutting tunnel controller'
            print
            return 

        else:
            print
            print 'Enter proper number'
            print


if __name__ == '__main__':
    main()
