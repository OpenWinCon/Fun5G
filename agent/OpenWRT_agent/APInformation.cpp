/*
 * APInformation.cpp
 *
 */

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
	fp = popen("ifconfig br-lan | grep HWaddr", "r");
	while (!feof(fp)) {
		fread(buf, 1, 64, fp);
	}
	temp.assign(buf);
	output = temp.substr(temp.find("HWaddr")+7, 17);

	m_ID = output;
	output.clear();
	temp.clear();
	pclose(fp);

	// Get IP
	fp = popen("ifconfig br-lan | grep inet", "r");
	fread(buf, 1, 64, fp);
	temp.assign(buf);
	output = temp.substr(temp.find("addr:")+5);
	output.erase(output.find(" "));

	m_IP = output;
	output.clear();
	temp.clear();
	pclose(fp);

	// Get SSID
	fp = popen("uci get wireless.@wifi-iface[0].ssid", "r");
	while (!feof(fp)) {
		fread(buf, 1, 64, fp);
	}
	temp.assign(buf);
	output = temp.substr(0, temp.find('\n'));

	m_SSID = output;
	output.clear();
	temp.clear();
	pclose(fp);

	//For Description
	m_Description.assign("Test date: 2015.10.18");
}
