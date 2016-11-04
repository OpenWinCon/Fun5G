/*
*/


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

#include "../headers/Report.h"
#include "../headers/Packet.h"
#include "../headers/Socket.h"
#include "../headers/Action.h"
#include "../headers/Database.h"
#include "../headers/protocol.h"


using namespace std;

void *Thread_Client(void *sock);
void *Thread_Heartbeat(void *trash);

Database db;

void disconnect(int signum)
{
	exit(0);
}

int main()
{
	signal(SIGINT, disconnect);

	pthread_t clientThread[1024];
	pthread_t HeartbeatThread;

	int i = 0;
	char trash;

	pthread_create(&HeartbeatThread, NULL, &Thread_Heartbeat, (void *)&trash);
	Socket ServSock;
	Socket cliSock[1024];
	ServSock.create();
	ServSock.bind(12015);
	ServSock.listen();

	while(ServSock.accept(cliSock[++i])) {
		cout << i << endl;
	pthread_create(&clientThread[i], NULL, &Thread_Client, (void *)&cliSock[i]);
	}
}


void *Thread_Client(void *sock) {
	cout << "Accept connection" << endl;
	Socket* socket = (Socket *)sock;

	while(true)
	{
		string msg;
		Packet pkt;
		socket->recv(msg);
		if(msg != "") {
			pkt.Parse(msg);
			Action act(pkt, &db, (Socket *)sock);
			msg = "";
		}
	}
	pthread_exit(0);
}

void *Thread_Heartbeat(void *trash)
{
	Action HB(&db);
	pthread_exit(0);
}
