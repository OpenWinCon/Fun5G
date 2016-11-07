# OVS에 ethernet을 위한 bridge를 추가하고 컨트롤러를 설정합니다.
ovs-vsctl add-br br-int
ovs-vsctl add-port br-int eth0
ifconfig eth0 0.0.0.0
ifconfig br-int 216.168.0.2 netmask 255.255.255.0
route add default gw 216.168.0.1 br-int
ovs-vsctl set-controller br-int tcp:192.168.0.2:6633

# OVS에 wireless lan을 위한 bridge를 추가하고 설정합니다.
ovs-vsctl add-br br-wlan
ovs-vsctl add-port br-wlan wlan0
ifconfig wlan0 0
ifconfig br-wlan 200.168.42.1 netmask 255.255.255.0
ovs-vsctl set-controller br-wlan tcp:192.168.0.2:6633

#ifconfig br-int 147.46.174.203 netmask 255.255.255.0
#ip rule add fwmark 1 table 200
#iptables -I OUTPUT -t mangle -d 216.168.0.2 -j MARK --set-mark 1
#sysctl -w net.ipv4.conf.tun0.rp_filter=2
#iptables -t nat -I POSTROUTING -o tun0 -j MASQUERADE
#ip route add default dev tun0 table 200
