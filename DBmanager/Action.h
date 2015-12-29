/*
 * Action.h
 *
 * Get Command or Packet by specific format (refer to PacketType)
 * Decide action by command or packet
 * Do proper action
 */

#ifndef ACTION_H_
#define ACTION_H_

#include "Packet.h"
#include "Database.h"
#include "Socket.h"
#include <string>
#include <iostream>

class Action {
public:
	// Information for action decision
	Packet m_GivenPkt;

	//local database;
	Database* m_db;

	//Socket
	Socket* m_sock;

public:
	Action();
	Action(Packet pkt, Database* db, Socket* sock);
	virtual ~Action();

	// Get Packet and command
	// If the parameter is string parse it.
	void GivePacket(Packet pkt);

private:
	// Decide proper action and perform the job.
	void TakeAction();

	//Add and update AP information on DB
	void InsertDatabase();
	void UpdateDatabase();

	//Send response to AP
	void SendResponseMessage(int messageType, Packet pkt);
};

#endif /* ACTION_H_ */
