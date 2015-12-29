#ifndef __PT_H__
#define __PT_H__
/********************
 @file protocol_hostap.h
 @date 2015/11/08
 @author 안계완, 고세원
 @brief
 	declare message types, and ap config structure
*********************/

#define AGENT_PORT 12014

#include <iostream>
#include <string>

using namespace std;

typedef enum Msg_types{
	mNULL = 0,
	START_AP = 1,
	STOP_AP,
	REBOOT,
	GET_STATUS,
	CHANGE_SSID,
	CHANGE_PWD,
	CHANGE_UPLINK,
	CHANGE_DOWNLINK,
	OFF_PWD,
	CHANGE_TX,
	CHANGE_CHANNEL,
	CHANGE_MODE,
	CHANGE_HIDE,
	BUILD_CONFIGURE,
	DISCONNECT,
	ACK
} msg_types;


typedef struct
{
	msg_types mtype;
	char param[256];
} Msg;

typedef struct {
	string interface;
	bool isBridge;
	string bridge;
	string ssid;
	string channel;
	string hw_mode;
	bool isPwd;
	string pwd;
	bool isHide;
	string hide;
} ap_config;


#endif
