/*
 * PacketType
 *
 * The Type of message or value is matched with specific number
 *
 * Each packet consist of following sequence.
 * MsgType(int)|ValueType(int)|Value(string)|ValueType(int)|Value(string)|...|END_OF_MESSAGE(int)
 */

#ifndef PACKETTYPE_H_
#define PACKETTYPE_H_


/********Message Type***********/
#define AP_REGISTRATION_REQUEST 0
#define AP_REGISTRATION_RESPONSE 1
#define AP_STATE_UPDATE_REQUEST 2
#define AP_STATE_UPDATE_RESPONSE 3
#define AP_LIST_REQUEST 4
#define AP_LIST_RESPONSE 5

/********Vaule Type***********/
#define AP_ID 0
#define AP_IP 1
#define AP_SSID 2
#define AP_DESCRIPTION 3
#define AP_PASSWORD 4
#define AP_HIDE 5
#define AP_CHANNEL 6

#define END_OF_MESSAGE 99 // Flag for end of message


static std::string ValueTypeToString(int type){
	switch(type){
	case 0:
		return "ID";
	case 1:
		return "IP";
	case 2:
		return "SSID";
	case 3:
		return "Description";
	case 4:
		return "Password";
	case 5:
		return "Broadcast";
	case 6:
		return "Channel";
	}
}
#endif /* PACKETTYPE_H_ */
