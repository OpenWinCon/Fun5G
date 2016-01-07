# TOX install

##Install
 CLI 버전인 toxic은 syntax error가 발생하여 GUI 버전인 qTox로 진행하였습니다.
 
 온라인(홈페이지) 방법 - Ign 문제로 다운 X
 
  ```
  $ echo " deb https://pkg.tox.caht/debian nightly $CODENAME " | sudo tee /etc/apt/sources.list.d/tox.list
  $ wget -qO - https://pkg.tox.chat/debian/pkg.gpg.key | sudo apt-key add – 
  $ sudo apt-get install apt-transport-https
  $ sudo apt-get update 
  ```
  
 웹상에 올라와 있는 바이너리 파일
 
  ```
  $ sudo wget https://build.tox.chat/view/Clients/job/qTox_build_linux_armhf_release/lastSuccessfulBuild/artifact/qTox_build_linux_armhf_release.tar.xz
  $ xz –d qTox_build_linux_armhf_release.tar.xz
  $ tar –xvf qTox_build_linux_armhf_release.tar
  $ cd qTox_build_linux_armhf_release
  $ sudo ./qTox
  ```
