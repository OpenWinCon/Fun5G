#ifndef __OW__
#define __OW__

#include <iomanip>
#include <cstdlib>
#include <iostream>
#include <unistd.h>
#include <string.h>
#include <sstream>
#include <fstream>

using namespace std;

class openwrt
{
public:
	openwrt();

	void reboot();
	void networkRestart();
	void commitWshaper();
	void get_status();	
	void show_status();

	bool set_ssid(string ssid);
	bool set_mode(string mode);
	bool set_channel(string channel);
	bool set_tx(string tx);
	bool set_password(string password);
	bool set_uplink(string uplink);
	bool set_downlink(string downlink);
	
	string get_ssid();
	string get_mode();
	string get_channel();
	string get_tx();
	string get_password();
	string get_uplink();
	string get_downlink();
private:
	string ssid;
	string mode;
	string channel;
	string tx;
	string password;
	string uplink;
	string downlink;
};

#endif
