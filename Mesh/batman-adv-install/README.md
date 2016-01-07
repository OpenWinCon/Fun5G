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

##Edison
batman-adv kernel module 빌드가 필요하다.

의존성 패키지 다운로드    
```
$ sudo apt-get install sed wget cvs subversion git-core coreutils |   
         unzip texi2html texinfo libsdl1.2-dev docbook-utils gawk |   
         python-pysqlite2 diffstat help2man make gcc build-essential |    
         g++ desktop-file-utils chrpath libgl1-mesa-dev libglu1-mesa-dev |    
         mercurial autoconf automake groff libtool xterm
```

####빌드 과정
edison 홈페이지에서 edison-src-xx.tar.gz 다운
```
$ tar -xvzf edison-src-xx.tar.gz
$ cd edison-src
$ sudo device-software/setup.sh --sdk_host=linux64 
  // (sdk_host란 빌드를 실행하는 운영체제를 뜻함)
$ source poky/oe-init-build-env
  //에러 발생 시, edison-src/build 폴더와 하위 파일에 권한 변경 후 oe-init-build-env 재시도   
  $ sudo chmod 777 edison-src/build -R
```		


config 파일 변경 or menuconfig로 빌드 설정 변경
___
#####config 파일 변경
`$ vi edison-src/device-software/meta-edison/recipes-kernel/linux/files/defconfig`

"# CONFIG_BATMAN_ADV is not set" 이라고 되어 있는 부분을
	CONFIG_BATMAN_ADV=m		
	CONFIG_BATMAN_ADV_BLA=y		
	CONFIG_BATMAN_ADV_DAT=y		
	CONFIG_BATMAN_ADV_NC=y		
으로 변경

#####menuconfig
`bitbake -c menuconfig virtual/kernel`

menuconfig에서 Networking support로 이동    
그 후 networking options으로 이동   
BATMAN-adv 관련된 옵션 활성화(B.A.T.M.A.N Advanced Meshing Protocol)
설정 save 후 종료
___

```
$ cp tmp/work/edison-poky-linux/linux-yocto/<bulid_version>/linux-edison-standard-build/.config ../device-software/meta-edison/recipes-kernel/linux/files/defconfig 
$ bitbake -c compile_kernelmodules virtual/kernel
$ bitbake edison-image
$ sudo ../device-software/utils/flash/postBuild.sh
```

####이미지 flash
edison-src/build/toFlash 하위 항목들을 edison에 flash

(https://software.intel.com/en-us/iot/hardware/edison/downloads)
여기에서 usb 드라이버 설치 후   
`$ sudo ./flashall.sh`    
windows 에서는 flashall.bat 실행

####batctl 설치
/etc/opkg/opkg.conf에 src edison_repo http://repo.opkg.net/edison/repo/core2-32/ 추가		
`$ opkg update & opkg install libnl batctl `

이후 과정은 PI과정과 동일(세팅파일)
```
$ sudo ifconfig wlan0 down
$ sudo iwconfig wlan0 mode ad-hoc essid junho_mesh channel 8
	//essid별로 네트워크 구축

$ sudo ifconfig wlan0 up
$ sudo modprobe batman-adv
 //-> load batman-adv module(이거 안 할 시, Error - batman-adv module has not been loaded 뜸)
 
$ sudo batctl if add wlan0
$ sudo ifconfig wlan0 mtu 1527
$ sudo cat /sys/class/net/wlan0/batman_adv/iface_status
	//"active"라고 떠야한다

$ sudo ifconfig bat0 up
//ip 할당은 생략(앞 페이지 참조)
$ sudo batctl o 

````
