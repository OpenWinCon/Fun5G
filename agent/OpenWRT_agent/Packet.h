/*
 * Packet.h
 *
 * Make packet in proper format.
 * Parse packet
 */

#ifndef PACKET_H_
#define PACKET_H_
#include <string>
#include <sstream>
#include <stdlib.h>
#include "PacketType.h"

class Packet {
public:
	// refer below PacketType
	int m_MsgType;
	int m_ValueType[16];
	std::string m_Value[16];

	//number of value in single packet
	int m_paramNo;

public:
	Packet();
	virtual ~Packet();

	// Decide Message Type and Add value
	void DecideMessageType(int msgType);
	void AddValue(int valueType, std::string value);

	// return standardized message string for transmission.
	std::string CreateMessage();

	// Parse message in message type, value type and value.
	void Parse(std::string msg);
};

#endif /* PACKET_H_ */
