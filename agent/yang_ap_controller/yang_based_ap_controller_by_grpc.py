# Copyright 2015, Google Inc.
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are
# met:
#
#     * Redistributions of source code must retain the above copyright
# notice, this list of conditions and the following disclaimer.
#     * Redistributions in binary form must reproduce the above
# copyright notice, this list of conditions and the following disclaimer
# in the documentation and/or other materials provided with the
# distribution.
#     * Neither the name of Google Inc. nor the names of its
# contributors may be used to endorse or promote products derived from
# this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
# LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
# A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
# OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
# LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
# DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
# THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

# next = dv.ap_list.add()
# next.ssid="sss"
"""
  #print(len(dv.ApList))
  print(dv.ap_list[0].ssid)
  print(dv.ap_list[1].ssid)

  new_AP = device_config_pb2.Devices.NetworkDevices.AccessPointInformation.ApList()
  new_AP.ssid = "new"

  dv.ap_list.extend([new_AP])

 # print (len(dv.ApList))
  print (dv.ApList[0].ssid)
  print (dv.ApList[1].ssid)
  print(dv.ApList[2].ssid)
"""
# apd = hostapd(dv)
# print(dv.ApList.ssid)
# print(apd.ssid)
# dv = device_config_pb2.Devices()
# dev = hostapd(dv)

# print (dev.ip)
# print (dev.ap.ssid)
# print (dev)
#
# print (dev.ap)
from __future__ import print_function

import grpc

import device_config_pb2
import device_config_pb2_grpc

from hostapd import hostapd


def run():
    while(1):
        command = input("wRFCONF> ")
        cmd_list = command.split()
        ip = cmd_list[0]
        cmd = cmd_list[1]
        length = len(cmd_list)
        channel = grpc.insecure_channel(ip + ':50051')
        stub = device_config_pb2_grpc.DeviceConfigStub(channel)
        if cmd == 'hello':
            reponse = stub.Hello(device_config_pb2.HelloRequest(hello='Hello'))
            print("Power: Off")
            print("IP: %s" % reponse.ip)
            print("SSID: %s" % reponse.ssid)
            print("channel: %s" % reponse.channel)
            print("mode: %s" % reponse.hw_mode)
            print("password: %s" % reponse.password)
        elif cmd == 'edit' or cmd == 'edit_config':
            if cmd_list[2] is None or cmd_list[3] is None:
                print("please enter value")
            else:
                reponse = stub.EditConfig(device_config_pb2.EditConfigRequest(command=cmd_list[2], value=cmd_list[3]))
                print("Power: Off")
                print("IP: %s" % reponse.ip)
                print("SSID: %s" % reponse.ssid)
                print("channel: %s" % reponse.channel)
                print("mode: %s" % reponse.hw_mode)
                print("password: %s" % reponse.password)






'''
    channel = grpc.insecure_channel('163.180.118.62:50051')
    stub = device_config_pb2_grpc.DeviceConfigStub(channel)

    print("---------------------------------------------------")
    print("Initial Session")
    print("---------------------------------------------------")
    print("Send Hello Request")
    print("---------------------------------------------------")
    reponse = stub.Hello(device_config_pb2.HelloRequest(hello='Hello'))
    print("Power: Off")
    print("IP: %s" % reponse.ip)
    print("SSID: %s" % reponse.ssid)
    print("password: openwincon_1")
    print("channel: %s" % reponse.channel)
    print("mode: %s" % reponse.hw_mode)
    print("broadcasting: on")
    print("---------------------------------------------------")

    reponse = stub.EditConfig(device_config_pb2.EditConfigRequest(command='start'))
    print("Send Edit Config Request(Start command)")
    print("---------------------------------------------------")
    print("Power: On")
    print("IP: 163.180.118.62")
    print("SSID: %s" % reponse.ssid)
    print("password: openwincon_1")
    print("channel: %s" % reponse.channel)
    print("mode: %s" % reponse.hw_mode)
    print("broadcasting: on")
    print("---------------------------------------------------")

    reponse = stub.EditConfig(device_config_pb2.EditConfigRequest(command='ssid', value='openwincon_test'))
    reponse = stub.EditConfig(device_config_pb2.EditConfigRequest(command='channel', value='8'))

    print("Send Edit Config Request(edit configuration command)")
    print("---------------------------------------------------")
    print("Power: On")
    print("IP: 163.180.118.62")
    print("SSID: %s" % reponse.ssid)
    print("password: openwincon_1")
    print("channel: %s" % reponse.channel)
    print("mode: %s" % reponse.hw_mode)
    print("broadcasting: on")
    print("---------------------------------------------------")
'''

# print(response)

def _edit_config(stub, in_command, in_value):
    print("Send Edit Config Request " + in_command)
    response = stub.EditConfig(device_config_pb2.EditConfigRequest(command=in_command, value=in_value))
    print("---------------------------------------------------")
    print("Power: On")
    print("IP: %s" % response.ip)
    print("SSID: %s" % response.ssid)
    print("password: %s" %response.password)
    print("channel: %s" % response.channel)
    print("mode: %s" % response.hw_mode)
    print("broadcasting: on")


# response = stub.SayHello(helloworld_pb2.HelloRequest(name='you'))
# print("Greeter client received: " + response.result)

def _hello(ip, port):
    channel = grpc.insecure_channel(ip + ':' + port)
    stub = device_config_pb2_grpc.DeviceConfigStub(channel)

    response = stub.Hello(device_config_pb2.HelloRequest(hello='Hello'))
    print("Power: Off")
    print("IP: %s" % response.ip)
    print("SSID: %s" % response.ssid)
    print("password: %s" %response.password)
    print("channel: %s" % response.channel)
    print("mode: %s" % response.hw_mode)
    print("broadcasting: on")
    print("---------------------------------------------------")

    return stub


if __name__ == '__main__':
    run()
