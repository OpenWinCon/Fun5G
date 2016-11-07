# -*- coding: utf-8 -*-

from netfilterqueue import NetfilterQueue
# import nfqueue
from scapy.all import *


SELECTIVE_FILTER_PATH_NAME = 'selective_filter_files'


proto_filter = {}
src_addr_filter = {}
dst_addr_filter = {}


def pkt_filter_callback(packet):
    #data = payload.get_data()
    pkt = IP(packet.get_payload())

    if pkt.proto in proto_filter:
        pkt_action = proto_filter[pkt.proto]
        if pkt_action == 'accept':
            packet.accept()

        elif pkt_action == 'drop':
            packet.drop()
            #payload.verdict(nfqueue.NF_DROP)

        elif pkt_action == 'reroute':
            print 'Packet Rerouting'
            packet.set_mark(2)
            packet.accept()

    if pkt.src in src_addr_filter:
        pkt_src_port, pkt_action = src_addr_filter[pkt.src]
        if pkt_src_port == '*' or pkt_src_port == pkt.sport:
            if pkt_action == 'accept':
                packet.accept()
                #payload.set_verdict(nfqueue.NF_ACCEPT)

            elif pkt_action == 'drop':
                packet.drop()
                #payload.set_verdict(nfqueue.NF_DROP)

    if pkt.dst in dst_addr_filter:
        pkt_dst_port, pkt_action = dst_addr_filter[pkt.dst]
        if pkt_dst_port == '*' or pkt_dst_port == pkt.dport:
            if pkt_action == 'accept':
                packet.accept()
                #payload.set_verdict(nfqueue.NF_ACCEPT)

            elif pkt_action == 'drop':
                packet.drop()
                #payload.set_verdict(nfqueue.NF_DROP)

            elif pkt_action == 'reroute':
                packet.set_mark(2)
                packet.accept()

    #payload.set_verdict_modified(nfqueue.NF_ACCEPT, str(pkt), len(pkt))

    '''
    if pkt.proto in proto_filter:
        pkt_action = proto_filter[pkt.proto]
        if pkt_action == 'accept':
            payload.set_verdict(nfqueue.NF_ACCEPT)

        elif pkt_action == 'drop':
            payload.set_verdict(nfqueue.NF_DROP)

    if pkt.src in src_addr_filter:
        pkt_src_port, pkt_action = src_addr_filter[pkt.src]
        if pkt_src_port == '*' or pkt_src_port == pkt.sport:
            if pkt_action == 'accept':
                payload.set_verdict(nfqueue.NF_ACCEPT)

            elif pkt_action == 'drop':
                payload.set_verdict(nfqueue.NF_DROP)

    if pkt.dst in dst_addr_filter:
        pkt_dst_port, pkt_action = dst_addr_filter[pkt.dst]
        if pkt_dst_port == '*' or pkt_dst_port == pkt.dport:
            if pkt_action == 'accept':
                payload.set_verdict(nfqueue.NF_ACCEPT)

            elif pkt_action == 'drop':
                payload.set_verdict(nfqueue.NF_DROP)

            elif pkt_action == 'reroute':
                payload.set_mark(2)

    #payload.set_verdict_modified(nfqueue.NF_ACCEPT, str(pkt), len(pkt))
    '''

def selective_forward_func():
    selective_filter_path = os.path.join(os.getcwd(), 'connect_module', 'AP_module', SELECTIVE_FILTER_PATH_NAME)
    proto_setting_fp = open(os.path.join(selective_filter_path, 'setting_protocol.csv'))
    addr_setting_fp = open(os.path.join(selective_filter_path, 'setting_address.csv'))

    proto_lines = proto_setting_fp.readlines()
    addr_lines = addr_setting_fp.readlines()

    for line in proto_lines:
        proto, action = line.replace('\n', '').split(',')

        proto_filter[int(proto)] = action

    for line in addr_lines:
        target, addr, port, action = line.replace('\n', '').split(',')

        try:
            if target == 'src':
                src_addr_filter[addr] = (int(port), action)

            elif target == 'dst':
                dst_addr_filter[addr] = (int(port), action)

        except ValueError:
            if target == 'src':
                src_addr_filter[addr] = (port, action)

            elif target == 'dst':
                dst_addr_filter[addr] = (port, action)

    '''
    q = nfqueue.queue()
    q.open()
    q.bind(socket.AF_INET)
    q.set_callback(pkt_modify)
    q.create_queue(1)
    '''

    nfq = NetfilterQueue()
    nfq.bind(1, pkt_filter_callback)

    try:
        print 'Starting to filtering packets'
        nfq.run()

    except KeyboardInterrupt:
        print 'Quitting selective mode'
        nfq.unbind()
