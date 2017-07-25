#!/bin/sh
#
#
# processname: yang_ap_server
# description: blahblah
# version: 0.1
#
#
export WRFCONF_HOME=/etc/wRFConf
case "$1" in
	start)
		echo -n "Starting yang_ap_server: "
		$WRFCONF_HOME/yang_based_ap_server_by_grpc.py
		echo
		;;
	stop)
		echo -n "Shutting down yang_ap_server: "
		echo
		;;
	restart)
		$0 stop
		sleep 2
		$0 start
		;;
	*)
		echo "Usage: $0 {start|stop|restart}"
		exit 1
esac
exit 0
