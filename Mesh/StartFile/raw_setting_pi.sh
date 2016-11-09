#!/bin/bash

sudo apt-get update
sudo apt-get install ssh git batctl -y
sudo apt-get install iw hostapd isc-dhcp-server -y
sudo apt-get install openvswitch-switch -y

sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java9-installer -y
sudo apt-get install openjdk-8-jdk tomcat8 -y
sudo apt-get install libglib2.0-dev libudev-dev libical-dev libreadline-dev bluez-tools -y

sudo wget http://launchpadlibrarian.net/214461570/bluez-hcidump_5.33-0ubuntu2_armhf.deb
sudo mv bluez-hcidump_5.33-0ubuntu2_armhf.deb ~/Downloads/bluez-hcidump_5.33-0ubuntu2_armhf.deb
sudo dpkg -i ~/Downloads/bluez-hcidump_5.33-0ubuntu2_armhf.deb

sudo git clone https://github.com/OpenWinCon/OpenWinNet.git ~/Downloads/OpenWinNet
sudo cp ~/Downloads/OpenWinNet/Mesh/StartFile/masterWebServer.war /var/lib/tomcat8/webapps

sudo service tomcat8 restart

sudo add-apt-repository ppa:abbat/tox
sudo apt-get update
sudo apt-get install qtox -y
