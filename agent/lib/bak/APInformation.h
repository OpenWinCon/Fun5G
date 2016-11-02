/*
 * APInformation.h
 *
 * Store and Update AP Information
 */
#ifndef APINFORMATION_H_
#define APINFORMATION_H_
#include <string>
#include <stdio.h>
#include <iostream>
#include "PacketType.h"

class APInformation {
private:
	std::string m_ID;
	std::string m_IP;
	std::string m_SSID;
	std::string m_Power;
	std::string m_Description;
	std::string m_Password;
	std::string m_Channel;
	std::string m_Hide;

public:
	//Set and get Information, use enum for numeric type.
	void SetAPInformation(int type, std::string value);
	std::string GetAPInformation(int type);

	//Update AP information from hardware.
	void UpdateAPInformation();
};

#endif /* APINFORMATION_H_ */
