#ifndef __DB_COMMAND_H__
#define __DB_COMMAND_H__


#include <iostream>
#include <iomanip>
#include <string>
#include "protocol.h"
#include "Socket.h"
#include "Packet.h"

class dbCommand 
{
public:
	dbCommand() { ; }
	dbCommand(string cand, int portNum);
	virtual ~dbCommand() { delete dbSock; }
	void showDB();
	string MakeDBRequest();

private:
	Socket* dbSock;


};



#endif
