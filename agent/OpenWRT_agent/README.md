#Hostapd Agent && Controller

##Introduction
이 프로젝트는 OpenWRT를 원격으로 관리하기 위한 프로그램과 **OpenWinNet**을 관리하기 위한 컨트롤러 프로그램 입니다. 이 프로젝트의 전체 구조는 다음과 같으며 이 중 ow_agent 대한 부분입니다.

![alt tag](https://github.com/OpenWinCon/OpenWinNet/blob/master/img/OpenWRT-agent.png)

##Environment
Raspberry Pi B2 OpenWRT + ipTIME N100UM Wireless LAN card


먼저 필요 패키지를 설치해야 합니다.

####무선 랜 설정을 위한 API 설치 
`$ opkg install kmod-cfg80211 kmod-mac80211`

####무선 랜 인식을 위한 USB 드라이버
`$ opkg install kmod-rt2800-lib kmod-rt2800-usb kmod-rt2x00-lib kmod-rt2x00-usb`


##Configuration

####AP 무선랜 설정
 ```
 $ vi /etc/config/network
 ...
 config interface 'lan'
 		option type 'bridge'
        option_orig_ifname 'eth0 wlan0'
        option_orig_bridge 'true'
        option ifname 'eth0'
        option proto 'dhcp'
        ...
 config interface 'wifi'
 		option proto 'static'
        option netmask '255.255.255.0'
        option ipaddr '192.168.222.1'
        
 ```
 
 
 ```
 $ vi /etc/config/wireless
 ...
 config wifi device 'wlan0'
 		option type 'mac80211'
        option hwmode '11g'
        option path 'platform/bcm2708_usb/usb/1-1/1-1.2/1-1.2:1.0'
        option htmode 'HT20'
        option country '00'
        option txpower '20'
        option channel '11'
        ...
 config wifi-iface
 		option device 'wlan0'
        option mode 'ap'
        option encryption 'psk'
        option network 'wifi'
        option key 'openwinnet'
        option ssid 'openwinnet'
 ```
 
 
 ```
 $vi /etc/config/dhcp
 ...
 config dhcp 'wifi'
 		option interface 'wifi'
        option start '100'
        option limit '150'
        option lease time '12h'
        ...
 ```
 
 ####대역폭 조절 추가 기능
 각 인터페이스 uplink, downlink 대역폭 조절
 `$ opkg install wshaper`
 
 대역폭 모니터링
 `$ opkg install iftop`
 
 
 
##Compile

make를 이용하여 컴파일 해 줍니다.

`$ make clean`
`$ make ow_agent`
`./ow_agent`

