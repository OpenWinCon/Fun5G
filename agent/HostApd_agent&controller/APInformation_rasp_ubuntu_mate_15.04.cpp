/*
 * APInformation.cpp
 *
 */

#include <string>
#include <cstring>
#include "APInformation.h"
using namespace std;

void APInformation::SetAPInformation(int type, string value) {
	switch (type) {
	case AP_ID:
		m_ID.assign(value);
		break;
	case AP_IP:
		m_IP.assign(value);
		break;
	case AP_SSID:
		break;
	case AP_DESCRIPTION:
		break;
	case AP_PASSWORD:
		m_Password.assign(value);
		break;
	case AP_CHANNEL:
		m_Channel.assign(value);
		break;
	case AP_HIDE:
		m_Hide.assign(value);
		break;
	default:
		break;
	}
}

string APInformation::GetAPInformation(int type) {
	switch (type) {
	case AP_ID:
		return m_ID;
	case AP_IP:
		return m_IP;
	case AP_SSID:
		return m_SSID;
	case AP_DESCRIPTION:
		return m_Description;
	case AP_PASSWORD:
		return m_Password;
	case AP_CHANNEL:
		return m_Channel;
	case AP_HIDE:
		return m_Hide;
	default:
		break;
	}
}


void APInformation::UpdateAPInformation(){

	FILE *fp;
	char buf[64];
	string output;
	string temp;

	// Get ID
	fp = popen("ifconfig wlan0 | grep HWaddr", "r");
	while (!feof(fp)) {
		fread(buf, 1, 64, fp);
	}
	temp.assign(buf);
	output = temp.substr(temp.find("HWaddr")+7, 17);
	cout << "MAC: " << output << endl;
	m_ID = output;
	output.clear();
	temp.clear();
	pclose(fp);

	// Get IP

	fp = popen("ifconfig wlan0 | grep inet", "r");
	fread(buf, 1, 64, fp);
	temp.assign(buf);
	output = temp.substr(temp.find("addr:")+5);
	output.erase(output.find(" "));

	m_IP = "192.168.0.46";
	cout <<"IP: " <<  m_IP << endl;
	output.clear();
	temp.clear();
	pclose(fp);

	// Get SSID
	fp = popen("iw wlan0 info| grep ssid", "r");
	while (!feof(fp)) {
		fread(buf, 1, 64, fp);
	}
	temp.assign(buf);
	if(temp.find("ssid") != std::string::npos)
	{
		output = temp.substr(temp.find("ssid")+5);
		output.erase(output.find("\n"));
	}
	else
	{
		output = "off";
	}
	cout << "SSID: " << output  << endl;
	m_SSID = output;
	output.clear();
	temp.clear();
	pclose(fp);

	//For Description
	m_Description.assign("Test date: 2015.11.04");
}
