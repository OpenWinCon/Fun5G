# BATMAN based Mesh and P2P

##Introduce
이 부분은 batman-adv 설치 guide 및 TOX 설치 guide를 포함하고 있으며, batmand-adv로 구성된 mesh network를 visualization 할 수 있는 GOTHAM 프로젝트에 대한 부분입니다.

##개발 환경
Raspberry Pi 2  
Ubuntu mate 15.04  
Linux-kernel version : 3.18.0-20-rpi2  
BATMAN-adv version : 2014.4.0  
BATCTL version : 2014.3.0-2  
JAVA version : 1.9.0-ea  
Web server : TOMCAT8

___
Intel Edison yocto 2.1  
Linux-kernel version : 3.10.17  
BATMAN-adv version : 2013.2.0  
BATCTL version : 2015.0

___
#####Edison kernel 빌드 computer
Ubuntu 12.04.5 64bit

___
#####GOTHAM 개발 및 빌드 computer
Mac OS X El Capitan  
Netbeans IDE 8.1  
JAVA version : 1.8.0_40



##[BATMAN-adv](https://github.com/OpenWinCon/OpenWinNet/tree/master/Mesh/batman-adv-install) 
 BATMAN(Better Approach To Mobile Ad hoc Network)를 구현해 놓은 프로그램입니다.
 
 *A simple pragmatic approach to mesh routing using BATMAN (2008) 참조
 
 
##[TOX](https://github.com/OpenWinCon/OpenWinNet/tree/master/Mesh/TOX-install)
 TOX란 중앙 서버가 없는 P2P 방식의 메신저로서 ID만으로 Client끼리 연결을 하는 프로그램입니다.
  
 
##[GOTHAM](https://github.com/OpenWinCon/OpenWinNet/tree/master/Mesh/GOTHAM(vis))
 이 프로그램은 batmand-adv로 구성된 mesh network를 visualization 해 줍니다.
