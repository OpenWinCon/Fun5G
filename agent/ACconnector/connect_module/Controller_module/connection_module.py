# -*- coding: utf-8 -*-

from netfilterqueue import NetfilterQueue
from scapy.all import *
import subprocess 
import sys

def pkt_filter_callback(packet):
    print packet

    packet.accept()


def packet_filter():
    nfq = NetfilterQueue()
    nfq.bind(1, pkt_filter_callback)

    try:
        print 'Controller packet filtering mode'
        nfq.run()

    except KeyboardInterrupt:
        print 'Quitting packet filter' 
        nfq.unbind()

        raise KeyboardInterrupt

