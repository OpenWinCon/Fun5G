/*
 * Action.cpp
 *
 */

#include "Action.h"

Action::Action() {
	// TODO Auto-generated constructor stub

}
Action::Action(Packet pkt, Database* db, Socket* sock){
	if(pkt.m_MsgType < 0){
		std::cout << "[E] No Information in Packet" << std::endl;
	}
	else{
		m_db = db;
		m_GivenPkt = pkt;
		m_sock = sock;
		TakeAction();
	}
}

Action::~Action() {
	// TODO Auto-generated destructor stub
}

// Get Packet and command
// If the parameter is string parse it.
void Action::GivePacket(Packet pkt){
	if(pkt.m_MsgType < 0){
		std::cout << "[E] No Information in Packet" << std::endl;
	}
	else{
		m_GivenPkt = pkt;
		TakeAction();
	}
}

// Decide proper action and perform the job.
void Action::TakeAction(){
	// Do action following message type
	Packet pkt;
	std::string result;
	std::string temp;

	switch(m_GivenPkt.m_MsgType){

	case AP_REGISTRATION_REQUEST:
		InsertDatabase();
		SendResponseMessage(AP_REGISTRATION_RESPONSE, pkt);
		break;

	//case AP_REGISTRATION_RESPONSE:
		//break;

	case AP_STATE_UPDATE_REQUEST:
		UpdateDatabase();
		SendResponseMessage(AP_STATE_UPDATE_RESPONSE, pkt);
		break;

	//case AP_STATE_UPDATE_RESPONSE:
		//break;

	case AP_LIST_REQUEST:
		result = m_db->GetResult("select ID, IP, SSID from AP_Information;");

		//Parse the result by '|' mark
		//and make pkt context
		while(true)
		{
			temp = result.substr(0,result.find('|'));
			result.erase(0,result.find('|')+1);
			pkt.AddValue(AP_ID, temp);

			temp = result.substr(0,result.find('|'));
			result.erase(0,result.find('|')+1);
			pkt.AddValue(AP_IP, temp);

			temp = result.substr(0,result.find('|'));
			result.erase(0,result.find('|')+1);
			pkt.AddValue(AP_SSID, temp);

			if(result.length() < 1) break;
		}
		SendResponseMessage(AP_LIST_RESPONSE, pkt);
		break;

	default:
		break;
	}
}


//Add AP information on DB
void Action::InsertDatabase(){
	std::string query;
	query.assign("INSERT INTO AP_Information (");

	for(int i=0; i<m_GivenPkt.m_paramNo; i++){
		query.append(ValueTypeToString(m_GivenPkt.m_ValueType[i]));
		query.append(",");
	}
	query.erase(query.length()-1);
	query.append(") VALUES ('");

	for(int i=0; i<m_GivenPkt.m_paramNo; i++){
			query.append(m_GivenPkt.m_Value[i]);
			query.append("','");
	}
	query.erase(query.length()-2);
	query.append(");");

	m_db->SendQuery(query);
}

//Update AP Information on DB
void Action::UpdateDatabase(){
	std::string query;
	query.assign("UPDATE AP_Information SET ");

	for(int i=1; i<m_GivenPkt.m_paramNo; i++){
		query.append(ValueTypeToString(m_GivenPkt.m_ValueType[i]));
		query.append("='");
		query.append(m_GivenPkt.m_Value[i]);
		query.append("',");
	}
	query.erase(query.length()-1);
	query.append(" WHERE ");
	query.append(ValueTypeToString(m_GivenPkt.m_ValueType[0]));
	query.append("='");
	query.append(m_GivenPkt.m_Value[0]);
	query.append("';");

	m_db->SendQuery(query);
}

//Send response to AP
void Action::SendResponseMessage(int messageType, Packet pkt){
	pkt.DecideMessageType(messageType);
	m_sock->send(pkt.CreateMessage());
	std::cout << "[S] (" << pkt.m_MsgType << ") ";
	std::cout << pkt.CreateMessage() << std::endl;
}
