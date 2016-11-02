#!/bin/bash

function init_lib() {
	echo "Initiation libraries"
	cd ./lib && make clean
	make
	echo "done"
	cd ../
}

function install_Controller() {
	echo "Install Controller"
	cd ./Controller
	make clean
	make
	echo "done"
	cd ../
}

function install_AP() {
	echo "install AP"
	cd ./AP-agent
	make clean
	make
	cd ../
	echo "done"
}

function all_clean() {
	echo "cleaning.."
	cd ./lib
	make clean
	cd ../
	cd ./Controller
	make clean
	cd ../
	cd ./AP-agent
	make clean
	cd ../
}


if [ $# -gt 1 ]; then
	echo "USAGE : '$0' [option]"
	exit 2
else
	if [ $# -eq 0 ]; then
		echo "Install ALL"
		init_lib
		install_Controller
		install_AP
	else
		if [ $1 == "all" ]; then
			init_lib
			install_Controller
			install_AP
		elif [ $1 == "controller" ]; then
			init_lib
			install_Controller
		elif [ $1 == "ap" -o $1 == "ap_agent" -o $1 == "AP_agent" ]; then
			init_lib
			install_AP
		elif [ $1 == "clean" -o $1 == "cl" ]; then
			all_clean
		else 
			echo "WRONG COMMAND!"
		fi
	fi
fi


