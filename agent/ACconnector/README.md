# AC connector
### Remote AP - Controller connector

AC connector는 원격에 있는 AP가 Controller의 control을 받을 수 있도록 하기 위한 기능을 제공하는 모듈 혹은 프로그램입니다.
AP와 Controller를 기존에 존재하는 터널 프로토콜 들을 사용해서 연결하고 필요에 따라서 해당하는
컨트롤 트래픽이나 데이터 트래픽을 터널을 통해서 전달합니다.

### Topology (Basic)
![Topology image](/README_images/topology.PNG)

#### 특이사항  
1. 여기서 각각의 원으로 표시된 개체는 ONOS를 제외하면 Ubuntu MATE가 설치된 라즈베리 파이입니다.
2. Forward Module이 있는 망과 ONOS가 있는 망은 서로 다른 subnet 주소를 사용해야 합니다.
~~3. 현재는 Forward module이 AP와 분리되어 있어야 합니다. 차후 이를 통합할 계획이 있습니다만 현재까지는 저렇게 모듈을 분리해 놓습니다.~~
3. Forward module을 AP에 통합하여 사용할 수 있습니다. 다만 특정한 설정을 필요로 하니 유의해야 합니다.

### 모듈 구성
1. Tunnel Controller
2. Connection Manager

### Tunnel Controller
Tunneling Protocol로는 OpenVPN을 사용합니다.

#### 서버 세팅
서버는 미리 OpenVPN 서버 패키지가 설치되어 있는 채로 작동하고 있습니다. 
OpenVPN 서버의 자세한 구성에 대해서는 https://www.digitalocean.com/community/tutorials/how-to-set-up-an-openvpn-server-on-ubuntu-14-04 를 참고해주세요. (여기서는 해당 문서에 나온 내용대로 서버를 구성했다고 가정하였습니다.)
서버에서는 기존에 생성되었던 ca.crt client.crt client.key client.csr를 모아서 client_keys.tar라는 파일로 작성해 놓고 접근 권한을 user level로 맞추어서 AP에서 이를 복사할 수 있도록 설정해 놓습니다.

#### AP 세팅
#### 프로그램 설명
프로그램은 2가지 모드로 작동합니다. 각각 setting mode와 connection mode로 setting mode 에서는 연결하고자 하는 OpenVPN 서버의 설정을 클라이언트에 저장해 두는 모드이고 connection mode는 setting mode에서 저장된 설정을 이용해 실제 서버에 연결하는 과정입니다.

자세한 동작 방식에 대해서는 tunnel_controller/openvpn 하위의 README를 참고해주세요.

### Connection Manager
이 모듈에서는 AP들에서 들어오는 (Control, Data) packet들을 올바른 목적지로 터널을 이용해 전송시키는 것을 목적으로 합니다.
AP가 연결된 물리 interface와 packet을 내보내야 할 tunnel을 프로그램에서 mapping 시켜놓고 이를 저장해두고
실제 forwarding을 수행하게 됩니다.

#### Forwarding modes
1. Full forward: 모든 패킷을 포워딩
2. Selective forward: 선택적으로 패킷을 포워딩
3. OF forward: Openflow 패킷만 포워딩

#### Forwarding method
1. ip rule에 특정 마크를 가진 패킷이 어떤 route table을 이용해야 하는지 설정  
  ```
  ip rule add fwmark 0 table 0
  ip route add default tun0 table 0
  ```    
  
2. Iptable을 이용해 물리 interface에서 들어오는 패킷 중 터널을 통해 전송해야 하는 패킷을 netfilter queue에 넣음
  ```
  iptables -t mangle -I PREROUTING -i eth0 -j NFQUEUE --queue-num 1
  ```

3. Netfilter queue에서 패킷이 터널을 통해야 하는 경우, 위에서 정한 마크 번호에 마춰 마킹을 한다.
  ```
  def pkt_callback(pkt): 
    pkt.set_mark(idx)
    pkt.accept
  return pkt_callback
  ```

해당 모듈에 관한 자세한 설명은 connect_module/AP_module의 README에 기록되어 있습니다.




