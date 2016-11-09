# ACconnector - Tunnel Controller

Tunnel Controller는 Remote AP와 ONOS subnetwork 사이에 tunnel을 만들고 관리하기 위한 프로그램입니다.
컨트롤러용 모듈 및 AP용 모듈 두 가지가 제공됩니다.
기본으로는 AP용 모듈이 설정되어 있습니다.

### Controller Module 실행
openvpn 폴더 아래의 Controller.py를 실행시킵니다.  
  `sudo python Controller.py`

#### Module specification
지원 기능

1. 현재 연결된 클라이언트 출력

  현재 연결된 클라이언트에 관련된 정보를 출력합니다.
  
2. 특정 클라이언트 차단

  현재 연결된 클라이언트 중 차단할 연결을 선택해서 차단합니다.  
  다만 현재는 해당 클라이언트에서 재접속하는 것을 막지는 못 합니다.


### AP Module 실행
openvpn 폴더에서 AP.py를 실행시키면 됩니다.  
  `sudo python AP.py`

#### Module Overview
프로그램은 2가지 모드를 지원합니다.  
1. Setting mode: 터널을 지원하는 서버 지정 및 해당 서버에서 key file등을 받아오는 과정을 수행  
2. Connection mode: 1의 모드에서 설정한 서버와 실제 연결하는 단계  


#### Module Interface - Setting mode
세팅 모드에서는 4가지로 구성된 메뉴가 나오는데 각각 하위에 정리된 것과 같습니다.

1. List current OpenVPN settings
  
  현재까지 프로그램에 추가된 모든 server의 설정을 출력합니다. 각각의 설정에 대해서 설정 이름, OpenVPN 서버의 IP 주소, 현재 연결 상태를 출력합니다.

2. Add new OpenVPN server
  
  새로운 서버의 설정을 추가합니다. 새 설정의 이름, 서버 주소 등을 등록합니다.
  이렇게 추가할 때에 server쪽에서는 새로운 connection 구축에 필요한 보안 key등을 받아오는 과정이 포함됩니다.

3. Remove OpenVPN server

  추가되었던 서버의 설정을 삭제합니다.
  
0. Quit

#### Module Interface - Connection mode
커넥션 모드는 5가지로 구성된 메뉴가 있습니다.

1. List current active OpenVPN connections
  
  현재 활성화된 OpenVPN 터널의 목록을 출력합니다. 현재는 활성화 상태를 직접 검사하지 않고 활성화 시킬 때 파일에 등록된 여부로만 검사해서 출력하므로 중간에 에러 등으로 인해 꺼지거나 아니면 처음에 제대로 시작되지 못한 경우에도 활성화 되었다고 나오게 됩니다.

2. Activate connection

  기존에 등록된 설정을 이용해 활성화 할 연결을 선택하고 활성화합니다.
  
3. Deactivate connection

  활성화 된 터널 중 종료할 터널을 설정해 종료시킵니다.
  
4. Check connection status
  
  활성화 된 터널의 endpoint에 핑을 보내는 방식을 이용해 터널의 상태를 검사합니다. 정상적으로 동작할 경우 ping response가 돌아오게 되므로 이를 확인하여 결과를 출력합니다.

0. Quit
