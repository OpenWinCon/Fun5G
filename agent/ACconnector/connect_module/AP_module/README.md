# ACconnector - Connect Module

Connect module은 미리 설정된 tunnel을 통해 실제로 Remote AP와 ONOS 사이에 패킷을 주고 받을 수 있도록 하는 모듈입니다.

### Topology
네트워크 구성은 제일 상위에 있는 README 파일에 설명한 것과 동일하다고 가정합니다.

### Module 실행
다음과 같은 코드를 이용해 프로그램을 실행합니다.
`sudo python connector.py`

### Module Interface
프로그램을 실행하면 5개의 항목이 나오므로 필요에 맞춰 실행합니다.

1. Check forward settings

  해당 항목에서는 유저가 설정해 두었던 forward setting을 출력합니다.
  각각의 forward setting은 들어오는 interface, 나가야하는 tunnel interface, forward mode로 구성되어 있습니다.
  forward mode는 3가지의 숫자로 표시되는데 아래와 같습니다.

  1 | Full forward  
  2 | Selective forward  
  3 | Openflow only forward  
  
2. Update forward setting

  해당 항목에서는 유저가 필요한 새로운 설정을 update할 수 있도록 하며 위의 forward setting에 필요한 정보를 입력하게 됩니다.
  포워딩하기를 원하는 인터페이스의 이름과 포워딩해서 내보낼 터널 인터페이스의 이름, 포워딩 모드를 차례로 입력하게 됩니다.
  만약 local에서 OVS를 같이 사용할 경우에는 처음에 인터페이스의 이름을 local로 설정하고 모드는 3으로 설정해 주어야 합니다.
  또한 외부로 나가는 interface의 OVS에는 controller를 설정할 수 없습니다. 그렇게 할 경우 remote에 있는 ONOS와 연결해야 하는 터널과 관련된 패킷도 이 설정으로 인해 나가지 못하게 되어서 결과적으로는 아예 패킷이 나가지 못하는 현상이 발생합니다. (즉, 터널과 외부 인터페이스에의 OVS는 같이 사용될 수 없습니다.)
  
3. Delete forward setting

  해당 항목에서는 유저가 기존에 입력해 놓은 설정 중 필요없는 항목을 삭제하는 것으로 들어오는 interface의 정보만 입력하면 삭제가 가능합니다.
  
4. Start forwarding
  
  해당 항목에서는 유저가 기존에 입력해놓은 대로 실제로 포워딩을 시작할 수 있도록 합니다. Forwarding을 수행하는 중에는 외부 입력을 받을 수 없으므로 중지시키려면 Ctrl^c 를 통해서 Interrupt를 발생시켜야 합니다.
  
0. Program quit
  
  해당 항목은 프로그램을 종료시킵니다.
