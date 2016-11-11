# -*- coding: utf-8 -*-

from netfilterqueue import NetfilterQueue
from scapy.all import *
import subprocess 
import sys

def pkt_filter_callback(packet):
    print packet

    packet.set_mark(10) 
    packet.accept()


def full_forward_func():
    nfq = NetfilterQueue()
    nfq.bind(1, pkt_filter_callback)

    cmd = 'iptables -t mangle -I PREROUTING -i eth1 -j NFQUEUE --queue-num 1'
    subprocess.call(cmd, shell=True, stdout=subprocess.PIPE)

    cmd = 'ip rule add fwmark 10 table full_forward'
    subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

    cmd = 'ip route add default dev tun0 table full_forward'
    subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

    cmd = 'iptables -t nat -I POSTROUTING -o tun0 -j MASQUERADE'
    subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

    cmd = 'sysctl -w net.ipv4.conf.tun0.rp_filter=2'
    subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

    try:
        print
        print 'Full forward mode - Starting to forwarding every packet'
        nfq.run()

    except KeyboardInterrupt:
        print
        print 'Quitting full forward mode'
        #nfq.unbind()

        cmd = 'iptables -t mangle -D PREROUTING 1'
        subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

        cmd = 'ip rule del table full_forward' 
        p1 = subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

        cmd = 'ip route flush table full_forward'
        p2 = subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

        cmd = 'iptables -t nat -D POSTROUTING 1'
        p3 = subprocess.call(cmd, stdout=subprocess.PIPE, shell=True)

    return
