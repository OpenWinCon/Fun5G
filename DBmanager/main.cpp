#include <iostream>
#include <pthread.h>
#include "Database.h"
#include "Socket.h"
#include "Packet.h"
#include "Action.h"
using namespace std;

void *Thread_Client(void *sock);

Database db;
const unsigned short PORT_NUMBER = 3000;

int main() {

    pthread_t clientThread[256];
	int i =0;

	Socket ServSock;
	Socket cliSock[256];
	ServSock.create();
	ServSock.bind(PORT_NUMBER);
	ServSock.listen();

	while(ServSock.accept(cliSock[++i])){
		cout << i << endl;
	    pthread_create(&clientThread[i], NULL, &Thread_Client, (void *)&cliSock[i]);
	}

	return 0;
}

void* Thread_Client(void *sock){

	cout << "Accept connection" << endl;
	Socket* socket = (Socket *)sock;

	while(true){
		string msg;
		Packet pkt;
		socket->recv(msg);
		if(msg != ""){
			pkt.Parse(msg);
			cout << "[R] (" << pkt.m_MsgType << ") " << pkt.CreateMessage() << endl;
			Action act(pkt, &db, (Socket *)sock);
			msg = "";
		}
	}
	pthread_exit(0);

}
