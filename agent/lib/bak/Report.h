/*
 * Report.h
 *
 * Report AP information and environment to Manger
 * If Manger send command, execute proper action.
 */

#ifndef REPORT_H_
#define REPORT_H_
#include "Packet.h"
#include "APInformation.h"
#include "Socket.h"
#include "hostap.h"
#include "Database.h"
#include "Action.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

const unsigned short PORT_NUMBER = 3000;
const unsigned short UPDATE_TIME = 10;

class Report {
private:

	// Baisc AP info
	APInformation m_APInfo;

	// Socket connection variable
	Socket* m_sock;
	Database m_db;

	// For time stamp to print on terminal
	time_t m_time;
	struct tm *m_ptm;
	std::string PrintTime();

	// Make connection with OpenWinNet Manager
	// When AP wake up, send basic AP Information
	// to OpenwinNet Manger for registration
	void Initialize();


	// to experiment
	void testBed(int i);

public:
	hostap* m_ap;
	Report(int i);
	Report(hostap *);
	Report();
	virtual ~Report();

	/*
	 * Send Message to manager
	 */
	void SendAPRegistrationRequest();
	void SendAPStateUpdateRequest();

};

#endif /* REPORT_H_ */
