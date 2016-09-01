#GOTHAM (Gathering of Organization Treating Humble Ad-hoc Map)

##Introduce
GOTHAM is mesh network agent with batman-adv using B.A.T.M.A.N routing protocol.
It supports mesh network visualization, self-organizing mesh network and SDN controller.
(SDN controller is developing)

This program used d3.js, websocket with JAVA.

##설치(installation)
java 설치(java installation)
```
$ sudo add-apt-repository ppa:webupd8team/java
$ sudo apt-get update
$ sudo apt-get install oracle-java9-installer
$ sudo apt-get install javacc
$ sudo apt-get install openjdk-8-jdk
```
tomcat 설치 (모든노드는 웹 서버 무조건 설치) 
(tomcat installation (every node must install web server))
```
$ sudo apt-get install tomcat8
$ cd GOTHAM(vis)/settingFile
$ cp masterWebServer.war /var/lib/tomcat8/webapps
$ sudo service tomcat8 restart
```


master일 경우 - batman-adv & batctl이 무조건 동작하고 있어야 함
(master - batman-adv & batctl must be running)
```
$ cd GOTHAM(vis)/settingFile/dist
$ sudo java -jar GOTHAM_hazel.jar wlan0 1 10.0.0.10
[arg 1 : interface name, arg2 : (1 : master, 2 : slave), arg3 : master node ip]
```

slave일 경우 - batman-adv & batctl & master가 무조건 동작하고 있어야 함     
(slvae - batmnad-adv & batctl & master must be running)
```
$ cd GOTHAM(vis)/settingFile/dist
$ sudo java -jar GOTHAM_hazel.jar wlan0 2 10.0.0.10
[arg 1 : interface name, arg2 : (1 : master, 2 : slave), arg3 : master node ip]
```

