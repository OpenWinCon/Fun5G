#Web-GUI

##Introduce
이 프로젝트는 hostapd를 관리하기 위한 프로그램과 **Openwinnet**을 관리하기 위한 컨트롤러 프로그램 입니다. 이 프로젝트의 전체 구조는 [그림 1]과 같으며 이 중 Web-GUI에 대한 부분입니다.

![alt tag](https://github.com/OpenWinCon/OpenWinNet/blob/master/agent/HostApd_agent%26controller/img/struct.png)

##Dependency
 이 프로그램은 다음과 같은 의존성을 가집니다.
 - python 3.4
 - python 가상 환경
 - django
 - python3.4-mysql connector
 
##Python3.4 및 django 설치
 python 3.4 및 django가 설치되어 있어야 합니다.
 만약 설치가 되어있지 않다면 [이 사이트](http://tutorial.djangogirls.org/ko/python_installation/index.html)를 참조할 것을 추천합니다.
 
##Python3.4-mysql Connector
 [이 곳](http://dev.mysql.com/downloads.connector/python)에서 자신의 OS에 맞는 Connector를 설치 후에 사용합니다.
 
##Before Started
 DB 연동을 위해 DB가 설치되어있는 IP주소를 변경해주어야 합니다.
 `/openwinnetsite/settings.py`
 ```
 ...
 ‘ENGINE’: ‘mysql.connector.django’,
 ...
 ‘USER’: ‘(user)’,
 ‘PASSWORD’: ‘(pwd)’, 
 ‘HOST’: ‘(DB IP)’
 ...  
 ```
 위의 user, pwd, IP주소를 맞게 변경한 뒤 사용하여야 합니다.
 
 ##Usage
 먼저 해당하는 프로젝트 디렉토리로 이동하여 줍니다.
 
 `$ cd (path)/project/web-gui`
 
 사용할 가상환경을 실행하여 줍니다.
 
 `$ source ./myvenv/bin/activate`
 
 그 다음 서버를 실행시켜 주면 Web-GUI의 구동이 완료되었습니다.

 `$ python ./manage runserver`


