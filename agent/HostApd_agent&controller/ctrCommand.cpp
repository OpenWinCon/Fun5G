#include "ctrCommand.h"
#include <iostream>
#include <iomanip>
#include <string>
#include <cstring>
#include <cstdlib>
#include <signal.h>
#include <netinet/in.h>
#include <resolv.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>

using namespace std;


/*********************************

 컨트롤러가 패킷을 생성하여 
 AGENT에게 전송 시키는 함수

**********************************/
ctrCommand::ctrCommand(string cand, char * cmd)
{
	if(cand == "help")
	{
		this->help();
	}
	else if(cand == "exit")
	{
		exit(0);
	}
	else if(cand == "cl" || cand == "clear")
	{
		system("clear");
		return;
	}
	else
	{
		bool validCmd;
		if((validCmd = is_ip(cand)) == false)
		{
			cout <<"Invalid IP, Please See help" << endl;
			return;
		}

		int sock;
		struct sockaddr_in serv_adr;

		sock = socket(PF_INET, SOCK_STREAM, 0);

		if(sock == -1)
		{
			cerr << "socket() error\n";
			return;
		}

		memset(&serv_adr, 0, sizeof(serv_adr));
		serv_adr.sin_family = AF_INET;
		serv_adr.sin_addr.s_addr = inet_addr(cand.c_str());
		serv_adr.sin_port = htons(AGENT_PORT);

		cout << "[C] Create Command Socket" << endl;
		
		if(connect(sock, (struct sockaddr *)&serv_adr, sizeof(serv_adr)) == -1)
		{
			cerr << "connect() error\n";
			return;
		}

		cout << "[C] Connecting to " << cand <<" Agent" << endl;
		
		if(strlen(cmd) <= 1)
		{
			cout <<"invalid Command" << endl;
			return;
		}
		Msg command;
		this->MakeCommandMessage(cmd, command);

		if(command.mtype != mNULL)
		{
			size_t cmd_size = sizeof(command);
			write(sock, (char *)&command, cmd_size);
			cout << "[S] Sending Command to " << cand << endl;

			Msg Reply;
			size_t reply_length;

			if( (reply_length = read(sock, (char *)&Reply, sizeof(Reply))) < 0)
			{
				cerr << "Read error\n";
				return;
			}

			if(Reply.mtype == ACK)
			{
				cout << Reply.param;
			}
			close(sock);
		}
			
	}


}




/*********************************

Agent가 명령어를 파싱하여 해당하는
명령어를 실행시키는 함수

**********************************/
ctrCommand::ctrCommand(Msg& cmd, hostap* ap)
{
	bool isSuccess = true;
	string errorM = ""; 
	/**************************
	 * process command by cmd.mtype
	 * if there is error, ack messase will be error message
	 * else, ack message will be status 
	 * ************************/
	switch(cmd.mtype)
	{
		case START_AP:
			cout << "[R] Recieve Start Command " << endl;
			if(ap->get_status() == "OFF")
				ap->start();
			else
			{
				isSuccess = false;
				errorM = "hostapd already is running\n";
			}
			break;
		case STOP_AP:
			cout << "[R] Recieve Stop Command " << endl;
			if(ap->get_status() == "ON")
				ap->stop();
			else
			{
				isSuccess = false;
				errorM = "hostapd is not running\n";
			}
			break;
		case REBOOT:
			cout << "[R] Recieve Reboot Command " << endl;
			if(ap->get_status() == "OFF")
				ap->start();
			else
				ap->restart();
			break;
		case GET_STATUS:
			cout << "[R] Recieve Status Command " << endl;
			break;
		case CHANGE_SSID:
			cout << "[R] Recieve Change SSID Command " << endl;
			if(isSuccess = ap->set_ssid(string(cmd.param)))
				ap->restart();
			else
				errorM = "SSID must be over 1 character\n";
			break;
		case CHANGE_PWD:
			cout << "[R] Recieve Change Password Command " << endl;
			if(isSuccess = ap->set_pwd(string(cmd.param)))
			{
				ap->pwd_on();
				ap->restart();
			}
			else
				errorM = "Password must be over 8 characters\n";
			break;
		case OFF_PWD:
			cout << "[R] Recieve Paswword OFF Command " << endl;
			ap->pwd_off();
			ap->restart();
			break;
		case CHANGE_CHANNEL:
			cout << "[R] Recieve Change Channel Command " << endl;
			if(isSuccess = ap->set_channel(string(cmd.param)))
				ap->restart();
			else
				errorM = "Channel must be in range ( 1 ... 11 )\n";
			break;
		case CHANGE_MODE:
			cout << "[R] Recieve Change Mode Command " << endl;
			if(isSuccess = ap->set_hwmode(string(cmd.param)))
				ap->restart();
			else
				errorM = "Mode must be 'g' or 'b'\n";
			break;
		case CHANGE_HIDE:
			cout << "[R] Recieve Change Hide Command " << endl;
			ap->set_hide(string(cmd.param));
			ap->restart();
			break;
		case CHANGE_UPLINK:
			cout << "[R] Recieve UPLINK" << endl;
			break;
		case CHANGE_DOWNLINK:
			cout << "[R] Recieve UPLINK" << endl;
			break;
		default:
			errorM = "Incorrect Command\n";
			break;
	}

	cmd.mtype = ACK;
	//if Error is not occured, make status message.
	if(isSuccess)
	{
		sprintf(cmd.param, "\nSTATUS: %s\nSSID: %s\nCHANNEL: %s\nMODE: %s\n", (ap->get_status()).c_str(), (ap->get_ssid()).c_str(), (ap->get_channel()).c_str(), (ap->get_hwmode()).c_str());

		if(ap->isPwd())
		{
			char temp[1024];
			sprintf(temp, "password: %s\n", (ap->get_pwd()).c_str());
			strcat(cmd.param, temp);
		}
		if(ap->isHide())
			strcat(cmd.param, "Hide: true\n");
	}
	else
		strcpy(cmd.param, errorM.c_str());
}


void ctrCommand::help()
{
	cout << "Commands:<IP> <Command> [value]  "<< endl;

	cout <<"  " << setw(20) << left << "help or h" << setw(25) << setfill(' ') << " show this usage help" << endl;

	cout <<"  " << setw(20) << left << "show" << setw(25) << setfill(' ') << " show ap's list" << endl;

	cout <<"  " << setw(20) << left << "start" << setw(25) << setfill(' ') << " start ap" << endl;

	cout <<"  " << setw(20) << left << "stop" << setw(25) << setfill(' ') << " stop ap" << endl;

	cout <<"  " << setw(20) << left << "reboot" << setw(25) << setfill(' ') << " reboot ap" << endl;

	cout <<"  " << setw(20) << left << "status" << setw(25) << setfill(' ') << " show ap's status" << endl;

	cout <<"  " << setw(20) << left << "ssid <value>" << setw(25) << setfill(' ') << " change ssid " << endl;

	cout <<"  " << setw(20) << left << "password [value]" << setw(25) << setfill(' ') << " change password(if params blank, off password) *** only hostap " << endl;

	cout <<"  " << setw(20) << left << "channel <value>" << setw(25) << setfill(' ') << " change channel " << endl;

	cout <<"  " << setw(20) << left << "mode <g,b>" << setw(25) << setfill(' ') << " change mode " << endl;

	cout <<"  " << setw(20) << left << "tx <value>" << setw(30) << setfill(' ') << " change txpower (range : 0~20) *** only openwrt" << endl;

	cout <<"  " << setw(20) << left << "uplink <value>" << setw(30) << setfill(' ') << " change AP's uplink bandwidth*** only openwrt" << endl;
	cout <<"  " << setw(20) << left << "downlink <value>" << setw(30) << setfill(' ') << " change AP's downlink bandwidth *** only openwrt" << endl;
	cout <<"  " << setw(20) << left << "hide <on, off>" << setw(25) << setfill(' ') << " broadcast on, off *** only hostap " << endl;

	cout <<"  " << setw(20) << left << "clear or cl" << setw(25) << setfill(' ') << " clear line" << endl;

	cout <<"  " << setw(20) << left << "quit" << setw(25) << setfill(' ') << " exit this program " << endl;

}

void ctrCommand::MakeCommandMessage(string str, Msg& cmd)
{
	using namespace std;
	/**
	  parsing command 
	Usage:
	start
	stop
	status
	ssid <param>
	password <param>
	channel <1~11>
	mode <g, n>
	hide <on, off>
	clear
	 **/
	if((str == "start") && (str.length() == 5))
	{
		cmd.mtype = START_AP;
		strcpy(cmd.param ,"");
	}
	else if((str == "stop") && (str.length() == 4))
	{
		cmd.mtype = STOP_AP;
		strcpy(cmd.param ,"");
	}
	else if((str == "reboot") && (str.length() == 6))
	{
		cmd.mtype = REBOOT;
		strcpy(cmd.param ,"");
	}
	else if((str.find("downlink") != std::string::npos) && (str.length() > 9))
	{
		cmd.mtype = CHANGE_DOWNLINK;
		strcpy(cmd.param ,(str.substr(9)).c_str());
	}
	else if((str.find("uplink") != std::string::npos) && (str.length() > 7))
	{
		cmd.mtype = CHANGE_UPLINK;
		strcpy(cmd.param ,(str.substr(7)).c_str());
	}
	else if((str == "status") && (str.length() == 6))
	{
		cmd.mtype = GET_STATUS;
		strcpy(cmd.param ,"");
	}
	else if((str.find("ssid") != std::string::npos) && (str.length() > 5))
	{
		cmd.mtype = CHANGE_SSID;
		strcpy(cmd.param ,(str.substr(5)).c_str());
	}

	else if(str.find("password") != std::string::npos)
	{
		if(str.length() == 8)
		{
			cmd.mtype = OFF_PWD;
			strcpy(cmd.param ,"");
		}
		else
		{
			string paswd = str.substr(9);

			if(paswd.length() < 8)
			{
				cout << "Password's length must be between 8 and 63" << endl;
				cmd.mtype = mNULL;

			}
			else
			{
				cmd.mtype = CHANGE_PWD;
				strcpy(cmd.param ,paswd.c_str());
			}
		}
	}
	else if((str.find("channel") != std::string::npos) && (str.length() > 8))
	{
		cmd.mtype = CHANGE_CHANNEL;
		strcpy(cmd.param ,(str.substr(8)).c_str());
	}
	else if((str.find("hide") != std::string::npos) && (str.length() > 5))
	{
		string temp;
		temp = str.substr(5);

		if(temp == "ON" || temp == "OFF" || temp == "on" || temp == "off")
		{
			cmd.mtype = CHANGE_HIDE;
			if(temp == "ON" || temp == "on")
				strcpy(cmd.param, "1");
			else
				strcpy(cmd.param, "0");
		}
		else
		{
			cmd.mtype = mNULL;
			cout << "Invalid Request, Please See help" << endl;
		}
	}
	else if((str.find("mode") != std::string::npos) && (str.length() > 5))
	{
		cmd.mtype = CHANGE_MODE;
		strcpy(cmd.param ,(str.substr(5)).c_str());
	}
	else if((str.find("tx") != std::string::npos) && (str.length() > 3))
	{
		cmd.mtype = CHANGE_TX;
		strcpy(cmd.param ,(str.substr(3)).c_str());
	}
	else if(str == "quit" && (str.length() == 4))
	{
		;
	}
	else if((str == "help" && (str.length() == 4)) || (str == "h" && (str.length() == 1)) )
	{
		cmd.mtype = mNULL;
		help();
	}
	else if((str == "clear" && (str.length() == 5)) || (str == "cl" && (str.length() == 2)) )
	{
		cmd.mtype = mNULL;
		system("clear");
	}
	else
	{
		cmd.mtype = mNULL;
		cout << "Invalid Command, Please see help" << endl;
	}

}




