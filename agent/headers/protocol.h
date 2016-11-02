#pragma once
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
#include <cstring>
#include <cstdlib>


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

static bool isOnlyNumber(char * temp)
{
	int length = strlen(temp);
	for(int i = 0; i < length; i++)
	{
		if(!isdigit(temp[i])) return false;
	}

	return true;
}

static bool is_ip(string cand)
{
	int candIP[4];
	int cand_cnt = 0;

	char temp[1024];
	char * result;

	strcpy(temp, cand.c_str());

	result = strtok(temp, ".");

	while(result != NULL)
	{
		if(isOnlyNumber(result))
		{
			if(cand_cnt == 4) return false;
			candIP[cand_cnt] = atoi(result);
			cand_cnt++;
		}
		result = strtok(NULL, ".");
	}
	if(cand_cnt != 4)       //underflow
		return false;
	for(int i = 0; i < 4; i++)      //range check
		if( candIP[i] < 0 || 255 < candIP[i] )
			return false;

	return true;

}


#endif
