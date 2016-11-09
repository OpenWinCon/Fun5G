# -*- coding: utf-8 -*-

from netfilterqueue import NetfilterQueue
from scapy.all import *
import subprocess 

def pkt_filter_callback(packet):
    print packet

    pkt = IP(packet.get_payload())

    try:
        if pkt[TCP].dport == 6633 or pkt[TCP].dport == 6653:
            packet.set_mark(30) 

    except:
        pass

    finally:
        packet.accept()


def of_forward_func():
    '''
    q = nfqueue.queue()
    q.open()
    q.bind(socket.AF_INET)
    q.set_callback(pkt_modify)
    q.create_queue(1)
    '''

    nfq = NetfilterQueue()
    nfq.bind(1, pkt_filter_callback)

    cmd = 'iptables -t mangle -I PREROUTING -i eth1 -p tcp --dport 6633 -j NFQUEUE --queue-num 1'
    subprocess.call(cmd, shell=True, stdout=subprocess.PIPE)

    cmd = 'ip rule add fwmark 30 table of_forward'
    subprocess.Popen(cmd.split(), stdout=subprocess.PIPE)

    cmd = 'ip route add default dev tun0 table of_forward'
    subprocess.Popen(cmd.split(), stdout=subprocess.PIPE)

    cmd = 'iptables -t nat -I POSTROUTING -o tun0 -j MASQUERADE'
    subprocess.Popen(cmd.split(), stdout=subprocess.PIPE)

    cmd = 'sysctl -w net.ipv4.conf.tun0.rp_filter=2'
    subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)


    try:
        print 'OF forward mode - Starting to forwarding Openflow packet'
        nfq.run()

    except KeyboardInterrupt:
        print 'Quitting Openflow packet forward mode'
        
        cmd = 'iptables -t mangle -D PREROUTING 1'
        subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

        cmd = 'ip rule del table of_forward' 
        subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

        cmd = 'ip route flush table of_forward'
        subprocess.Popen(cmd, stdout=subprocess.PIPE, shell=True)

        cmd = 'iptables -t nat -D POSTROUTING 1'
        subprocess.Popen(cmd, stdout=subprocess.PIPE, shell=True)

    return
