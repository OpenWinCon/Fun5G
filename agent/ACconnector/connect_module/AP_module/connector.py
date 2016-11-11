# -*- coding: utf-8 -*-


from netfilterqueue import NetfilterQueue
from scapy.all import *
from multiprocessing import Process

import subprocess 
import signal
import os
import pickle


class SettingManager:
    file_name = '/etc/iproute2/forward.settings'
    idx = 0

    def __init__(self):

        try:
            with open(self.file_name, 'r') as fp:
                self.settings = pickle.load(fp)

        except IOError:
            fp = open(self.file_name, 'w')
            fp.close()
            self.settings = {}

        except EOFError:
            self.settings = {}

        self.set_new_idx() 

    def print_settings(self):
        print
        print 'Printing forward settings'
        print
        
        print_target_lst = ['Index', 'Incoming Interface', 'Forward Interface', 'Mode']
        col_width = max(len(target) for target in print_target_lst) + 2
        print ''.join(target.ljust(col_width) for target in print_target_lst)
        
        for k, v in sorted(self.settings.iteritems()):
            print ''.join(target.ljust(col_width) for target in 
                    [str(v['idx']), k, v['forward_inter'], str(v['mode'])])


    def update_setting(self):
        print 'Enter ethernet interface name: ',
        inter_name = raw_input()

        print 'Enter forwarding interface name: ',
        forward_inter = raw_input()

        print 'Enter forwarding mode: ',
        forward_mode = int(raw_input())

        if inter_name in self.settings:
            print
            print 'This interface is already used by another rule'
            print
            return
            
        self.settings[inter_name] = {'forward_inter': forward_inter, 'mode': forward_mode, 'idx': self.idx}
        self.idx += 1

        if forward_mode == 2:
            filter_file_path = os.path.join(os.getcwd(), 'selective_filter_files', inter_name)
            if not os.path.exists(filter_file_path):
                os.makedirs(filter_file_path)
                open(os.path.join(filter_file_path, 'setting_address.csv'), 'w').close()
                open(os.path.join(filter_file_path, 'setting_protocol.csv'), 'w').close()

        with open(self.file_name, 'w') as fp:
            pickle.dump(self.settings, fp)


    def delete_setting(self):
        self.print_settings()
    
        print
        inter_name = raw_input('Enter interface name: ')

        if inter_name not in self.settings:
            print 'Rule does not exists'
            print
            return

        self.settings.pop(inter_name, None)

        with open(self.file_name, 'w') as fp:
            pickle.dump(self.settings, fp)


    def get_settings(self):
        return self.settings


    def set_new_idx(self):
        idx = 0
        for k, v in self.settings.iteritems():
            idx = max(v['idx'], idx)

        idx += 1

        self.idx = idx


def make_pkt_callback(idx):
    def pkt_callback(pkt):
        print 'Nor: ',
        print pkt
        pkt.set_mark(idx)
        pkt.accept()
    return pkt_callback


def make_selective_pkt_callback(idx, filters):
    proto_filter = filters['proto_filter']
    addr_filter = filters['addr_filter']
   
    proto_type = filters['proto_type']
    addr_type = filters['addr_type']

    def pkt_sel_callback(pkt):
        print 'Sel: ', 
        print pkt
        #data = payload.get_data()
        ip_pkt = IP(pkt.get_payload())
        
        # print ip_pkt.proto, proto_filter
        if ip_pkt.proto in proto_filter:
            pkt_action = proto_filter[ip_pkt.proto]
            if pkt_action == 'accept':
                pkt.set_mark(idx)
                pkt.accept()
                return

            elif pkt_action == 'drop':
                pkt.drop()
                return
                #payload.verdict(nfqueue.NF_DROP)

            elif pkt_action == 'reroute':
                #print 'Packet Rerouting'
                pkt.set_mark(idx)
                pkt.accept()
                return


        if ip_pkt.src in addr_filter:
            pkt_src_port, pkt_action = addr_filter[ip_pkt.src]
            if pkt_src_port == '*' or pkt_src_port == ip_pkt.sport:
                if pkt_action == 'accept':
                    pkt.set_mark(idx)
                    pkt.accept()
                    #payload.set_verdict(nfqueue.NF_ACCEPT)

                elif pkt_action == 'drop':
                    pkt.drop()
                    #payload.set_verdict(nfqueue.NF_DROP)

        if ip_pkt.dst in addr_filter:
            pkt_dst_port, pkt_action = addr_filter[ip_pkt.dst]
            if pkt_dst_port == '*' or pkt_dst_port == ip_pkt.dport:
                if pkt_action == 'accept':
                    pkt.set_mark(idx)
                    pkt.accept()
                    #payload.set_verdict(nfqueue.NF_ACCEPT)

                elif pkt_action == 'drop':
                    pkt.drop()
                    #payload.set_verdict(nfqueue.NF_DROP)

                elif pkt_action == 'reroute':
                    pkt.set_mark(idx)
                    pkt.accept()
        
        pkt.set_mark(idx)
        pkt.accept()

    return pkt_sel_callback  


def check_tables():
    fp = open('/etc/iproute2/rt_tables', 'r')

    lines = list(map(lambda x: x.replace('\n', ''), fp.readlines()))

    tables = {}

    if len(lines) > 11:
        for line in lines[11:]:
            if not line.startswith('#'):
                info = line.split('\t')
                table_code = int(info[0])
                table_name = info[1]
                
                tables[table_code] = table_name

    if 10 not in tables:
        tables[10] = 'full_forward'

    if 20 not in tables:
        tables[20] = 'selective_forward'

    if 30 not in tables:
        tables[30] = 'of_forward'

    fp = open('/etc/iproute2/rt_tables', 'w')
    for line in lines[:11]:
        fp.write(line + '\n')

    for k, v in sorted(tables.iteritems(), reverse=True):
        fp.write('%d\t%s\n' % (k, v))
    fp.close()


def make_filters(inter_name):
    filter_file_path = os.path.join(os.getcwd(), 'selective_filter_files', inter_name)

    addr_fp = open(os.path.join(filter_file_path, 'setting_address.csv'), 'r')
    proto_fp = open(os.path.join(filter_file_path, 'setting_protocol.csv'), 'r')

    addr_lines = list(map(lambda x: x.replace('\n', ''), addr_fp.readlines()))
    addr_filter = {}
    
    if len(addr_lines) == 0:
        addr_type = None

    else:
        addr_type = addr_lines[0]
        addr_lines = addr_lines[1:]

        for addr_line in addr_lines:
            addr_info = addr_line.split(',')
            addr_filter[addr_info[0]] = [addr_info[1], addr_info[2]]

    proto_lines = list(map(lambda x: x.replace('\n', ''), proto_fp.readlines()))
    proto_filter = {}

    if len(proto_lines) == 0:
        proto_type = None

    else:
        proto_type = proto_lines[0]
        proto_lines = proto_lines[1:]
    
        for proto_line in proto_lines:
            proto_info = proto_line.split(',')
            proto_filter[int(proto_info[0])] = proto_info[1]

    filters = {'addr_filter': addr_filter, 'addr_type': addr_type, 'proto_filter': proto_filter, 'proto_type': proto_type}
    return filters


def nfq_worker(nfq, idx, callback_type, filters=None):
    signal.signal(signal.SIGINT, signal.SIG_IGN)

    if callback_type == 1:
        nfq.bind(idx, make_pkt_callback(idx))

    else:
        nfq.bind(idx, make_selective_pkt_callback(idx, filters))

    nfq.run() 


def start_forwarding(settings):
    process_lst = []
    idx = 1

    for inter_name, forward_dic in sorted(settings.iteritems()):
        nfq = NetfilterQueue()
        forward_inter, forward_mode = forward_dic['forward_inter'], forward_dic['mode']
        
        #nfq.bind(idx, lambda pkt: print pkt; pkt.set_mark(idx); pkt.accept())
        print forward_inter, forward_mode, idx 
        if forward_mode in [1,3]:
            nfq_pr = Process(target=nfq_worker, args=(nfq, idx, 1))
#            nfq.bind(idx, make_pkt_callback(idx))

        elif forward_mode == 2:
            filters = make_filters(inter_name)
            nfq_pr = Process(target=nfq_worker, args=(nfq, idx, 2, filters))
#            nfq.bind(idx, make_selective_pkt_callback(idx, filters))

        process_lst.append(nfq_pr)

        if inter_name == 'local' and forward_mode == 3:
            cmd = 'iptables -t mangle -I OUTPUT -p tcp --dport 6633 -j NFQUEUE --queue-num %d' % idx

        elif forward_mode == 1 or forward_mode == 2:
            cmd = 'iptables -t mangle -I PREROUTING -i %s -j NFQUEUE --queue-num %d' % (inter_name, idx)

        elif forward_mode == 3:
            cmd = 'iptables -t mangle -I PREROUTING -i %s -p tcp --dport 6633 -j NFQUEUE --queue-num %d' % (inter_name, idx)
        
        subprocess.call(cmd, shell=True, stdout=subprocess.PIPE)

        cmd = 'ip rule add fwmark %d table %d' % (idx, idx)
        subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

        cmd = 'ip route add default dev %s table %d' % (forward_inter, idx)
        subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

        cmd = 'iptables -t nat -I POSTROUTING -o %s -j MASQUERADE' % forward_inter
        subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

        cmd = 'sysctl -w net.ipv4.conf.%s.rp_filter=2' % forward_inter
        subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

        idx += 1

    print
    print 'Start forwarding by rules'

    for pr in process_lst:
        pr.start()

    try:
        while True:
           time.sleep(1)

#        nfq.run()

    except KeyboardInterrupt:
        print
        print 'Quitting forwarding mode'

        for pr in process_lst:
            pr.terminate()
            pr.join()
#        nfq.unbind()

        idx = 1
        for inter_name, forward_dic in sorted(settings.iteritems()):
            forward_inter, forward_mode = forward_dic['forward_inter'], forward_dic['mode'] 

            if forward_mode in [1,2]:
                cmd = 'iptables -t mangle -D PREROUTING -i %s -j NFQUEUE --queue-num %d' % (inter_name, idx)

            elif forward_mode == 3 and inter_name != 'local':
                cmd = 'iptables -t mangle -D PREROUTING -i %s -j NFQUEUE --queue-num %d -p tcp --dport 6633' % (inter_name, idx)

            else:
                cmd = 'iptables -t mangle -D OUTPUT -p tcp --dport 6633 -j NFQUEUE --queue-num %d' % idx

            subprocess.call(cmd, shell=True, stdout=subprocess.PIPE)

            cmd = 'ip rule del table %d' % idx
            subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

            cmd = 'ip route flush table %d' % idx
            subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

            cmd = 'iptables -t nat -D POSTROUTING -o %s -j MASQUERADE' % forward_inter
            subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

            idx += 1

    return 
            

def control_delegation():
    #check_tables()
    setting_manager = SettingManager()
    #setting_manager.print_settings()
    #setting_manager.update_setting()

    # cmd = 'iptables -A INPUT -i br-lan -j NFQUEUE --queue-num 1'
    # cmd = 'iptables -A FORWARD -i eth1 -j NFQUEUE --queue-num 1'
    # cmd = 'iptables -A OUTPUT -d 8.8.8.8 -j NFQUEUE --queue-num 1'

    #cmd = 'sysctl -w net.ipv4.conf.ppp0.rp_filter=2'
    #p2 = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE)

    #p1.kill()
    #p2.kill()

    while True:
        print 
        print '(AP - controller) connector - Main mode'
        print 'Select mode'
        print '1. Check forward settings'
        print '2. Update forward setting'
        print '3. Delete forward setting'
        print '4. Start forwarding'
        print '0. Program quit'

        try:
            print 'Select mode: ',
            selection = int(raw_input())

            if selection not in range(0, 5):
                raise ValueError

        except ValueError:
            print 'Enter proper number'
            continue

        if selection == 1:
            setting_manager.print_settings()

        elif selection == 2:
            setting_manager.update_setting()
        
        elif selection == 3:
            setting_manager.delete_setting()

        elif selection == 4:
            start_forwarding(setting_manager.get_settings())

        elif selection == 0:
            print
            print 'Quitting connector program'
           
            return


if __name__ == '__main__':
    try:
        control_delegation()

    except:
        pass

    finally:
        #cmd = 'iptables -D FORWARD 1'
        #subprocess.Popen(cmd.split(), stdout=subprocess.PIPE)
        pass




