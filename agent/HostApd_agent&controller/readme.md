#Hostapd Agent && Controller

##Introduce
이 프로젝트는 hostapd를 관리하기 위한 프로그램과 **Openwinnet**을 관리하기 위한 컨트롤러 프로그램 입니다. 이 프로젝트의 전체 구조는 [그림 1]과 같으며 이 중 AP-agent, CLI, Controller에 대한 부분입니다.

![alt tag](https://github.com/OpenWinCon/OpenWinNet/blob/master/img/HostApd-agent.png)

##Install
 먼저 필요 패키지를 설치해야 합니다.
 
 `$ apt-get install bridge-utils iw hostapd isc-dhcp-server`
 
 설치 후에는 사용을 위한 **환경변수 설정**이 필요합니다.
 ```
 $ export AP_INTERFACE=(wireless card interface)
 $ export BACKBONE_INTERFACE=(backbone interface)
 [$ export TUNNEL_INTERFACE=(tunnel interface)]
 ```

##Configure Forwarding
 **hostapd**를 사용하기 위해서는 먼저 시스템의 포워딩을 활성화 해 주어야 합니다. 아래의 설정을 **반드시** 해 주어야 합니다.

 먼저 커널의 포워딩을 활성화 시켜줍니다.
 
 `$ sysctl -w net.ipv4.ip_forward=1`
 
 혹은 `/etc/sysctl.conf` 파일의 
 
 `net.ipv4.ip_forward=1`
 의 주석을 삭제해주거나 추가해주어야 합니다.
 
 그 다음 iptable이 설치되어 있는 컴퓨터라면 iptable의 fowarding과 masquerading을 활성화 시켜줍니다.
 
 ```
 $ iptables -P FORWARD ACCEPT
 $ iptables --table nat -A POSTROUTING -o (BACKBONE_INTERFACE) -j MASQUERADE
 ```
 
 혹은 `/etc/rc.loacl` 파일에 아래의 코드를 추가하여 줍니다.
 
 ```
 /sbin/iptables -P FORWARD ACCEPT
 /sbin/iptables --table nat -A POSTROUTING -o (BACKBONE_INTERFACE) -j MASQUERADE
 ```
##Configure dhcpd
 
 이 프로그램의 경우 ap마다 dhcp를 사용함으로 dhcpd의 설치 및 설정이 필요합니다. 같이 첨부된 **예제**를 사용할 것을 **추천**합니다.
 
 `/etc/dhcpd/dhcpd.conf` 파일을 열고 아래의 예제와 같이 내용을 수정하여 줍니다.
 ```
 ddns-update-style none;
 option domain-name “example.org";
 option domain-name-servers 192.168.0.1;

 default-lease-time 600;
 max-lease-time 7200;

 log-facility local7;

 subnet 192.168.0.0 netmask 255.255.255.0 {
      option routers 192.168.0.34;
 }
 subnet 192.168.1.0 netmask 255.255.255.0 {
	  range 192.168.1.35 192.168.1.80;
      option routers 192.168.1.34;
 }
 ```
##Before Compiling
 컴파일 하기에 앞서 파일 중 일부를 **반드시 수정**해주어야 합니다.
 
 먼저 `Report.cpp` 파일의 DB 서버와 연결되는 부분을 수정해주어야 합니다.
 ```
 ...
 int Report::initialize()
 ...
 m_sock.connect(“(DB IP)”, PORT_NUMBER);
 ...
 ```
 IP를 DB 매니저가 실행 되고 있는 IP로 변경하여 줍니다.
 
 Controller가 DB와 연결되는 부분 역시 수정해주어야 합니다.
 `controller.cpp` 중
 
 ```
 ...
 int main()
 ...
 dbCommand db(“(DB IP)”, PORT_NUMBER);
 ...
 ```
 
 위 IP를 DB 매니저가 실행 되고 있는 IP로 변경하여 줍니다.
 
 다음 설정은 만약 예제로 주어진 dhcpd를 사용한다면 **생략**하여도 상관 없습니다.
 
 `hostap.cpp` 
 ```
 ...
 int hostap::start()
 ...
 sprintf(temp, “ifconfig %s (DHCPD GATEWAY IP)” …
 ...
 ```
 위 IP 부분을 dhcpd의 gateway로 바꾸어 줍니다.
 
 
##Compile

make를 이용하여 컴파일 해 줍니다.

`$ make`

실행을 위해 권한을 추가하여 줍니다.

`$ chmod +x ap_agent controller command`



**다음 프로그램들을 실행할 땐 반드시 root권한으로 실행하여야 합니다.**
##AP_Agent Usage
`Usage: $ ap_agent [hostapd conf file path(default:./conf/openwinnet.conf)]`

##Controller Usage
`Usage: $ controller`

```
Commands:<IP> <Command> [value]
 help or h			show this usage help
 show				show ap's list
 start 				start ap
 stop 				stop ap
 reboot 			reboot ap
 status				show ap's status
 ssid <value>		change ssid
 password [value]	change password(if params blank, off password)
 channel <value>	change channel
 mode <value>		change mode
 tx <value>			change txpower
 uplink <value>		change AP's uplink bandwidth
 downlink <value> 	change AP's downlink bandwidth
 hide <on, off> 	ap hide on, off
 clear or cl		clear line
 quit				exit this program
```



