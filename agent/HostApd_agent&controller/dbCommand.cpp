#include <iostream>
#include <iomanip>
#include <string>


#include "protocol.h"
#include "Socket.h"
#include "Packet.h"
#include "dbCommand.h"


using namespace std;

dbCommand::dbCommand(string cand, int portNum)
{
	dbSock = new Socket();
	dbSock->create();
	cout << "[c] Create Socket" << endl;
	if(!is_ip(cand))
	{
		cout << "[E] Invalid IP, Please See help" << endl;
		return;
	}

	dbSock->connect(cand, portNum);
	cout << "[C] Connecting DB" << endl;
}


string dbCommand::MakeDBRequest()
{
	Packet pkt;
	pkt.DecideMessageType(AP_LIST_REQUEST);
	string msg = pkt.CreateMessage();
	return msg;

}



void dbCommand::showDB()
{
	string RequestMessage = MakeDBRequest();

	dbSock->send(RequestMessage);
	cout << "[R] Request AP List" << endl;

	cout << setfill('-') << setw(34) << "[Openwinnet AP LIST]" << setw(17) << "" << endl;
	cout << setfill(' ') << left << "|" << setw(19) << "ID" << setw(16) << "IP" << setw(14) << "SSID" << "|" << endl;

	while(true)
	{
		string Ack;
		Packet pkt;
		dbSock->recv(Ack);
		if(Ack != "")
		{
			pkt.Parse(Ack);
			if(pkt.m_MsgType == AP_LIST_RESPONSE)
			{
				for(int i = 0; i < pkt.m_paramNo/3; i++)
				{
					cout << "|" << setw(19) << pkt.m_Value[i*3 + 0] << setw(16)  << pkt.m_Value[i*3 + 1] << setw(14) << pkt.m_Value[i*3 + 2] << "|" << endl;
				}
			}
			break;
		}
	}
	cout << setfill('-') << setw(51)  << "" << endl;

}

