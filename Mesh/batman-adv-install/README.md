# Batman-adv install guide

##Pi
batman-adv는 kernel에 존재하기 때문에, 따로 설치가 필요 X

####batctl 설치
`$ sudo apt-get install batctl `

####세팅 파일 실행
master node부터 실행    
`$ sudo python first_setting_master.py `

실행 시 "active"라고 커맨드창에 떠야한다.   
혹시 "Error - batman-adv module has not been loaded" 라는 메시지가 출력 시    
문제 커널에 모듈이 없던지, 제대로 실행이 되지 않은 상태   
`$ sudo modeprobe batman-adv`   
확인 후 기타 다른 방법으로 해결

####DHCP server 설치
master node에는 무조건 dhcp server가 존재해야 한다.   
dhcp server 설치 후 설정파일 수정   
```
$ sudo apt-get install isc-dhcp-server
$ sudo vi /etc/dhcp/dhcpd.conf
```
  예시)
  subnet 192.168.0.0 netmask 255.255.255.0 {
            range 192.168.0.100 192.168.0.200
            option routers 192.168.0.1
  }
```
$ sudo service isc-dhcp-server restart
$ sudo service --status-all
```

이 때 모든 interface가 static ip를 가지고 있어야 한다.    
만약 그렇지 않을 시 isc-dhcp-server가 start가 안됨    
마지막 명령 후 isc-dhcp-server [+]인지 반드시 확인    
그 후   
`$ sudo batctl gw server`
