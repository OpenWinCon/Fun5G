#include <iostream>
#include <pthread.h>
#include "Database.h"
#include "Socket.h"
#include "Packet.h"
#include "Action.h"
using namespace std;

void *Thread_Client(void *sock);
void *Thread_Heartbeat(void *trash);

Database db;
const unsigned short PORT_NUMBER = 3000;

int main() {

    pthread_t clientThread[800];
	pthread_t HeartbeatThread;
	int i =0;
	char trash;
	
	pthread_create(&HeartbeatThread, NULL, &Thread_Heartbeat, (void *)&trash);

	Socket ServSock;
	Socket cliSock[800];
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

void* Thread_Heartbeat(void* trash) {
	
	Action HB(&db);
	pthread_exit(0);
} 
