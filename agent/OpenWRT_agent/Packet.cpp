/*
 * Packet.cpp
 *
 */

#include "Packet.h"
#include <iostream>
using namespace std;

Packet::Packet() {
	this->m_paramNo = 0;

}
Packet::~Packet() {

}

// Decide Message Type and Add value
void Packet::DecideMessageType(int msgType) {
	m_MsgType = msgType;
}
void Packet::AddValue(int valueType, string value) {
	m_ValueType[m_paramNo] = valueType;
	m_Value[m_paramNo] = value;
	m_paramNo++;
}


// return standardized message string for transmission.
string Packet::CreateMessage() {

	string msg;
	stringstream sstr;

	// Message composed of type and value set and token(|) between them.
	// please refer to PacketType.h file
	sstr.str("");
	sstr << m_MsgType;
	msg.assign(sstr.str());
	msg.append("|");

	for (int i=0; i < m_paramNo; i++) {
		sstr.str("");
		sstr << m_ValueType[i];
		msg.append(sstr.str());
		msg.append("|");

		msg.append(m_Value[i]);
		msg.append("|");
	}

	sstr.str("");
	sstr << END_OF_MESSAGE;
	msg.append(sstr.str());
	msg.append("\0");

	return msg;
}


// Parse message in message type, value type and value.
void Packet::Parse(string msg) {

	//Get Message Type
	m_MsgType = atoi(msg.substr(0,1).data());
	msg.erase(0,2);

	//Get Value Type and Value until end of message
	int type=0;
	string temp;
	while(true)
	{
		type = atoi(msg.substr(0,msg.find('|')).data());
		if(type == END_OF_MESSAGE)	break;
		msg.erase(0,msg.find('|')+1);

		temp = msg.substr(0,msg.find('|'));
		msg.erase(0,msg.find('|')+1);

		AddValue(type, temp);
	}
}
