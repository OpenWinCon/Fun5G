/*
 * Report.cpp
 *
 */

#include "Report.h"
#include "hostap.h"

#include <iostream>
#include <iomanip>
#include <sstream>
#include <time.h>
using namespace std;

string Report::PrintTime(){

	m_ptm = localtime(&m_time);
	string strTime = asctime(m_ptm);
	strTime.erase(strTime.length()-5);

	return strTime;
}

Report::Report() {
	// TODO Auto-generated constructor stub
	Initialize();
}
Report::Report(hostap* ap) {

	// TODO Auto-generated constructor stub
	m_ap = ap;
	Initialize();
}

Report::~Report() {
	// TODO Auto-generated destructor stub
}


/***********************************************
 *void Report::Initialize()
 *	Make connection with OpenWinNet Manager
 *	Send Wake-Up Message to OpenWinNet Manager
 *	Receive Wake-Up-Response Message.
 ********************************************/
void Report::Initialize() {

	// Make connection with OpenWinNetManager
	cout << "sock create" << endl;
	m_sock.create();
	m_sock.connect("127.0.0.1", PORT_NUMBER);
	cout << "sock connetcted" << endl;

	string msg;
	Packet pkt;

	// Get AP Information from hardware.
    	m_APInfo.UpdateAPInformation();
	m_APInfo.SetAPInformation(AP_PASSWORD, m_ap->get_pwd());
	if(m_ap->get_hide() == "1" )
		m_APInfo.SetAPInformation(AP_HIDE, "on");
	else	
		m_APInfo.SetAPInformation(AP_HIDE, "off");
	m_APInfo.SetAPInformation(AP_CHANNEL, m_ap->get_channel());

   	// Send AP-Registration-Request Message to OpenWinNetManager
    	SendAPRegistrationRequest();

    	// Receive Response about Request message.
	while(true){
		m_sock.recv(msg);
		if(msg != ""){
			pkt.Parse(msg);
			if(pkt.m_MsgType == AP_REGISTRATION_RESPONSE){
				cout << PrintTime() << "[R] AP_REGISTRATION_RESPONSE" << endl;
				break;
			}
		}
	}

    // Send AP_STATE_UPDATE_REQUEST Message.
	while(true){
		sleep(UPDATE_TIME);	// check report.h to find update_time
	    m_APInfo.UpdateAPInformation();
		m_APInfo.SetAPInformation(AP_PASSWORD, m_ap->get_pwd());
		m_APInfo.SetAPInformation(AP_CHANNEL, m_ap->get_channel());
		
		if(m_ap->get_hide() == "1" )
			m_APInfo.SetAPInformation(AP_HIDE, "on");
		else	
			m_APInfo.SetAPInformation(AP_HIDE, "off");
		SendAPStateUpdateRequest();

		// Receive Response about Request message.
		while(true){
			m_sock.recv(msg);
			cout << msg << endl;
			
			if(msg != ""){
				pkt.Parse(msg);
				if(pkt.m_MsgType == AP_STATE_UPDATE_RESPONSE){
					cout << PrintTime() << "[R] AP_STATE_UPDATE_RESPONSE" << endl;
					break;
				}
			}
		}
	}

}
/***********************************************
 *void Report::SendAPRegistrationRequest()
 *	Make AP Registration Request
 *	Send Message to Manager
 *	Receive ACK from Manager
 ********************************************/
void Report::SendAPRegistrationRequest(){

	//Make Message
	Packet pkt;
	pkt.DecideMessageType(AP_REGISTRATION_REQUEST);


	for(int type=AP_ID; type<=AP_CHANNEL; type++){
		pkt.AddValue(type, m_APInfo.GetAPInformation(type));
	}
	/*
	for(int type=AP_ID; type<=AP_DESCRIPTION; type++){
		pkt.AddValue(type, m_APInfo.GetAPInformation(type));
	}
	*/
	string msg = pkt.CreateMessage();


	//Send Message to OpenWinNet Manager
	cout << PrintTime() << "[S] AP_REGISTRATION_REQUEST" << endl;
	m_sock.send(msg);
	cout << "Send.." << endl;
	msg.clear();
}

/***********************************************
 *void Report::SendAPUpdateRequest()
 *	Make AP Update Request
 *	Send Message to Manager
 *	Receive ACK from Manager
 ********************************************/
void Report::SendAPStateUpdateRequest(){
	//Make Message
	Packet pkt;
	pkt.DecideMessageType(AP_STATE_UPDATE_REQUEST);

	for(int type=AP_ID; type<=AP_CHANNEL; type++){
		pkt.AddValue(type, m_APInfo.GetAPInformation(type));
	}
	/*
	for(int type=AP_ID; type<=AP_DESCRIPTION; type++){
		pkt.AddValue(type, m_APInfo.GetAPInformation(type));
	}*/
	string msg = pkt.CreateMessage();

	//Send Message to OpenWinNet Manager
	cout << PrintTime() << "[S] AP_STATE_UPDATE REQUEST" << endl;
	m_sock.send(msg);
	msg.clear();
}
