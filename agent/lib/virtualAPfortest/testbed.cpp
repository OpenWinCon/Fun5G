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
#include <cstdlib>
#include <ctime>

#include "../../headers/protocol.h"
#include "../../headers/hostap.h"
#include "../../headers/Report.h"
#include "../../headers/ctrCommand.h"

using namespace std;

const int max_length = 1024;

	
void* Thread_Sender(void *arg)
{
	char* argv = (char *)arg;
	int i = atoi(argv);
	Report sender(2);
	pthread_exit(0);
}


int main(int argc, char* argv[])
{
		pthread_t sender;
		srand((unsigned int)time(NULL));

		cout << "Testbed" << endl;
		cout << "Copyright 2015 Kyung Hee University Mobile Convergence Lab" << endl;

			pthread_create(&sender, NULL, &Thread_Sender, (void *)argv[1]);
		pthread_join(sender, NULL);


	return 0;
}
