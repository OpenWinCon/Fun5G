#GOTHAM convergence version

##Introduce
Master & Slave programs are in one program


master일 경우 - batman-adv & batctl이 무조건 동작하고 있어야 함        
(master - batman-adv & batctl must be running)
```
$ sudo java -jar GOTAHM.jar wlan0 1 192.168.0.10
[arg1 : interface name, arg2 : (1 : master, 2 : slave), arg3 : master node ip]
```

slave일 경우 - batman-adv & batctl & master가 무조건 동작하고 있어야 함   
(slvae - batmnad-adv & batctl & master must be running)
```
$ sudo java -jar GOTAHM.jar wlan0 2 192.168.0.10
[arg1 : interface name, arg2 : (1 : master, 2 : slave), arg3 : master node ip]
```


