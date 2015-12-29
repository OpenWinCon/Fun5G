#include <cstdlib>
#include <signal.h>
#include <iostream>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <pthread.h>
#include "struct_openwrt.h"
#include "openwrt.h"
#include "Report.h"


#define BUF_SIZE 1024

using namespace std;

openwrt* ow;

int serv_sock, clnt_sock;

void process_Cmd(Msg& cmd)
{
        bool issuccess = true;
        string errormsg = "";
        switch(cmd.mtype)
        {
        case REBOOT:
                ow->reboot();
                break;
        case CHANGE_SSID:
                if(issuccess = ow->set_ssid(string(cmd.param)))
                {
                        ow->networkRestart();
                        cout << "change ssid complete" << endl;
                }
                else
                {
                        errormsg = "change ssid failed";
                        cout << errormsg << endl;
                }
                break;
        case CHANGE_MODE:
                if(issuccess = ow->set_mode(string(cmd.param)))
                {
                        ow->networkRestart();
                        cout << "change hwmode complete" << endl;
                }
                else
                {
                        errormsg = "change hwmode failed";
                        cout << errormsg << endl;
                }
                break;
        case CHANGE_CHANNEL:
                if(issuccess = ow->set_channel(string(cmd.param)))
                {
                        ow->networkRestart();
                        cout << "change channel complete" << endl;
                }
                else
                {
                        errormsg = "change channel failed";
                        cout << errormsg << endl;
                }
                break;


	case CHANGE_TX:
                if(issuccess = ow->set_tx(string(cmd.param)))
                {
                        ow->networkRestart();
                        cout << "change txpower complete" << endl;
                }
                else
                {
                        errormsg = "change txpower failed";
                        cout << errormsg << endl;
                }
                break;
	case CHANGE_PWD:
		if(issuccess = ow->set_password(string(cmd.param)))
		{
			ow->networkRestart();
			cout << "change password complete" << endl;
		}
		else
		{
			errormsg = "change password failed";
			cout << errormsg << endl;
		}
		break;
	case CHANGE_UPLINK:
		if(issuccess = ow->set_uplink(string(cmd.param)))
		{
			ow->commitWshaper();
			cout << "change bandwidth uplink complete" << endl;
		}
		else
		{
			errormsg = "change bandwidth uplink failed";
			cout << errormsg << endl;
		}
		break;
	case CHANGE_DOWNLINK:
		if(issuccess = ow->set_downlink(string(cmd.param)))
		{
			ow->commitWshaper();
			cout << "change bandwidth downlink complete" << endl;
		}
		else
		{
			errormsg = "changet bandwidth downlink failed";
			cout << errormsg << endl;
		}
		break;
        case GET_STATUS:
                break;
        default:
                break;
        }

	cmd.mtype = ACK;

        if(issuccess)
        {
		ow->get_status();
		ow->show_status();
	
                sprintf(cmd.param, "\nSSID: %s\nMODE: %s\nCHANNEL: %s\nTX_POWER: %s\nPASSWORD: %s\nUPLINK: %s\nDOWNLINK: %s", (ow->get_ssid()).c_str(), (ow->get_mode()).c_str(), (ow->get_channel()).c_str(), (ow->get_tx()).c_str(), (ow->get_password()).c_str(), (ow->get_uplink()).c_str(), (ow->get_downlink()).c_str());
       
	}
        else
                strcpy(cmd.param, errormsg.c_str());


}

void* Thread_Sender(void *arg)
{
	Report sender;
	pthread_exit(0);
}

void* Thread_Commander(void *arg){

        struct sockaddr_in serv_adr;
        struct sockaddr_in clnt_adr;
        socklen_t clnt_adr_sz;

	try
        {
                serv_sock = socket(PF_INET, SOCK_STREAM, 0);
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
			process_Cmd(cmd);
			if(write(clnt_sock,(char*)&cmd, sizeof(cmd)) < 0)
			{
				perror("write");
				break;
			}

	                        close(clnt_sock);
                }

                close(serv_sock);
        }
        catch(std::exception& e)
        {
                std::cerr << "Exception: " << e.what() << "\n";
        }

	pthread_exit(0);
}

int main(int argc, char* argv[])
{
	try
	{
		pthread_t commander;	
		pthread_t sender;

		ow = new openwrt();
	
		cout << "AP Agent Start" << endl;	
		
		pthread_create(&commander, NULL, &Thread_Commander, (void *)NULL);
		pthread_create(&sender, NULL, &Thread_Sender, (void *)NULL);

		pthread_join(commander, NULL);
		pthread_join(sender, NULL);

	}
	catch(std::exception& e)
	{
		std::cerr << "Exception: " << e.what() << "\n";
	}
	return 0;
}
