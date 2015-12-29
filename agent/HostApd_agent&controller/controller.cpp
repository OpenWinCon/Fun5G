/********************
 @file controller.cpp
 @date 2015/12/01
 @author 안계완
 @brief Manager program
 	if you know AP agent's ip,
 	you can control AP agent
*********************/

#include <cstdlib>
#include <cstring>
#include <string>
#include <signal.h>
#include <iostream>
#include <iomanip>
#include <netinet/in.h>
#include <resolv.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <pthread.h>

#include "Report.h"
#include "Packet.h"
#include "Socket.h"
#include "protocol.h"
#include "dbCommand.h"

using namespace std;
enum { max_length = 1024 };

void disconnect(int signum)
{
	exit(0);
}

int main(int argc, char* argv[])
{

	signal(SIGINT, disconnect);
	try
	{
		if (argc != 1)
		{
			std::cerr << "Usage: " << argv[0] << endl;
			return 1;
		}

		cout << endl << endl << "Openwinnet Controller v0.6" << endl;
		cout << "Copyright 2015 Kyung Hee University Mobile Convergence Lab" << endl;
		cout << "All rights reserved." << endl;
		cout << "For info, please contact to roy1022@hanamil.net " << endl << endl << endl;
	
	
		dbCommand db("127.0.0.1", PORT_NUMBER);
		while(1)
		{
			std::cout << std::endl << "Openwinnet manager> ";
		
			string cand;
			bool validCmd;
			char request[max_length];
			char command[1024];

			cin >> cand;
			cin.getline(request, max_length);
			if(cand == "db" || cand == "show")
			{
				db.showDB();
				continue;
			}
			sprintf(command, "./command %s %s", cand.c_str(), request+1);
			FILE *fp;

			fp = popen(command, "r");
			ostringstream output;

			while( !feof(fp) && !ferror(fp) )
			{
				char buf[128];
				int bytesRead = fread( buf, 1, 128, fp);
				output.write(buf, bytesRead);
			}
			string result = output.str();
			cout << result;
			pclose(fp);
			//system(command);
			bzero(request, sizeof(request));
			
		}
	}
	catch (std::exception& e)
	{
		std::cerr << "Exception: " << e.what() << "\n";
	}

	return 0;
}
