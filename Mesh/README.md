# BATMAN based Mesh and P2P

##Introduce
이 부분은 batman-adv 설치 guide 및 TOX 설치 guide를 포함하고 있으며, batmand-adv로 구성된 mesh network를 visualization 할 수 있는 GOTHAM 프로젝트에 대한 부분입니다.

##Install
 먼저 필요 패키지를 설치해야 합니다.
 
 `$ apt-get install bridge-utils iw hostapd isc-dhcp-server`
 
 설치 후에는 사용을 위한 **환경변수 설정**이 필요합니다.
 ```
 $ export AP_INTERFACE=(wireless card interface)
 $ export BACKBONE_INTERFACE=(backbone interface)
 [$ export TUNNEL_INTERFACE=(tunnel interface)]
 ```
