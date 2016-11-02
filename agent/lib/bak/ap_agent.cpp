/********************
 @file ap_agent.cpp
 @date 2015/12/2
 @author 안계완
 @brief processing message from mananger program
*********************/

#include <cstdlib>
#include <cstring>
#include <string>
#include <signal.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <iostream>
#include <pthread.h>
#include <fstream>

#include "protocol.h"
#include "hostap.h"
#include "Report.h"
#include "ctrCommand.h"

using namespace std;

const int max_length = 1024;


hostap* ap;			//AP object to control hostap

int serv_sock, clnt_sock;
	
void* Thread_Sender(void *arg)
{
	Report sender(ap);
	pthread_exit(0);
}

void* Thread_Commander(void *arg)
{
	struct sockaddr_in serv_adr;
	struct sockaddr_in clnt_adr;
	socklen_t clnt_adr_sz;

	serv_sock = socket(PF_INET, SOCK_STREAM, 0);
	int opt = 1;
	setsockopt(serv_sock, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));
	try
	{
		if(serv_sock == -1)
		{
			cout << "socket() error\n";
			exit(1);
		}	

		memset(&serv_adr, 0, sizeof(serv_adr));
		serv_adr.sin_family = AF_INET;
		serv_adr.sin_addr.s_addr = htonl(INADDR_ANY);
		serv_adr.sin_port = htons(AGENT_PORT);
	
		if(bind(serv_sock, (struct sockaddr*)&serv_adr, sizeof(serv_adr)) == -1)
		{
			cout << "bind() error\n";
			exit(1);
		}

		if(listen(serv_sock,100) == -1)
		{
			cout << "listen() error\n";
			exit(1);
		}

		clnt_adr_sz = sizeof(clnt_adr);

		while(1)
		{
			clnt_sock = accept(serv_sock, (struct sockaddr*)&clnt_adr, &clnt_adr_sz);
			if(clnt_sock == -1)
			{
				cout << "accept() error\n";
				exit(1);
			}
			Msg cmd;
			read(clnt_sock,(char *)&cmd, sizeof(cmd));
			cout << "[R] Receivced Command" << endl;
			cout << cmd.mtype << " " << cmd.param << endl;
			ctrCommand(cmd, ap);
			cout << "[P] Processed Command" << endl;
			if( write(clnt_sock, (char *)&cmd, sizeof(cmd)) < 0)  
			{
				perror("write");
				break;
			}
			cout <<"[S] Sending Ack" << endl;
			close(clnt_sock);
			cout << "[C] Close Sock" << endl;

		}
	}
	catch(std::exception& e)
	{
		std::cerr << "Commander Exception: " << e.what() << "\n";
	}
	pthread_exit(0);
}

void closeServer(int signum)
{
	//if occur SIGINT, kill dhcpd, hostapd
	close(serv_sock);
	system("skill dhcpd");
	system("skill hostapd");
	exit(0);
}

int main(int argc, char* argv[])
{
	try
	{
		pthread_t sender;
		pthread_t commander;
		signal(SIGINT, closeServer);

		//exception handling
		if (argc > 2)
		{
			std::cerr << "Usage: " << argv[0] << " [confPath]\n";
			return 1;
		}

		//manual conf path
		if(argc == 2)
			ap = new hostap(argv[1]);
		else
			ap = new hostap("./conf/openwinnet.conf"); // default confPath
		cout << endl << endl << endl;
		cout << "HostAP Manager Server v0.6" << endl;
		cout << "Copyright 2015 Kyung Hee University Mobile Convergence Lab" << endl;
		cout << "All rights reserved." << endl;
		cout << "For info, please contact to roy1022@hanamil.net " << endl << endl << endl;

		pthread_create(&sender, NULL, &Thread_Sender, (void *)NULL);
		pthread_create(&commander, NULL, &Thread_Commander, (void *)NULL);

		pthread_join(sender, NULL);
		pthread_join(commander, NULL);	
	}
	catch (std::exception& e)
	{
		std::cerr << "Exception: " << e.what() << "\n";
	}

	return 0;
}
