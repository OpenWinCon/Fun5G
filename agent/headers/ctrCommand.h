#ifndef __COMMAND_H__
#define __COMMAND_H__
#include <iostream>
#include <iomanip>
#include <string>

#include "protocol.h"
#include "Socket.h"
#include "Packet.h"
#include "hostap.h"

using namespace std;

class ctrCommand
{
public:
	ctrCommand() { ; }
	ctrCommand(string cand, char * cmd);
	ctrCommand(Msg& cmd, hostap* ap);
	virtual ~ctrCommand() { ; }

	void MakeCommandMessage(string str, Msg& cmd);
	void showDB();
	void help();
	string MakeDBRequest();


private:

};

#endif
