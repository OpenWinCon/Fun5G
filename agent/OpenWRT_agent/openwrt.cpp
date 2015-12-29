#include "openwrt.h"

openwrt::openwrt()
{
	get_status();
}

void openwrt::reboot()
{
	system("/sbin/reboot");
}

void openwrt::networkRestart()
{
	system("/sbin/uci commit wireless");
	sleep(2);
	system("/etc/init.d/network restart");
}

void openwrt::commitWshaper()
{
	system("/sbin/uci commit wshaper");
	sleep(2);
	system("/etc/init.d/network restart");
}

void openwrt::get_status()
{
	string str = "";

	ifstream in("/etc/config/wireless");

	if(in.is_open())
	{
		while(!in.eof())
		{
			getline(in, str);
		
			if((str.find("option ssid") != std::string::npos))
			{
				this->ssid = str.substr(str.find("option ssid") + 11);
			}	
			else if((str.find("option hwmode") != std::string::npos))
			{
				this->mode = str.substr(str.find("option hwmode") + 13);
			}
			else if((str.find("option channel") != std::string::npos))
			{
				this->channel = str.substr(str.find("option channel") + 14);
			}
			else if((str.find("option txpower") != std::string::npos))
			{
				this->tx = str.substr(str.find("option txpower") + 14);
			}
			else if((str.find("option key") != std::string::npos))
			{
				this->password = str.substr(str.find("option key") + 10); 
			}
		}
	}

	in.close();

	in.open("/etc/config/wshaper");

	if(in.is_open())
	{
		while(!in.eof())
		{
			getline(in, str);

			if((str.find("option uplink") != std::string::npos))
			{
				this->uplink = str.substr(str.find("option uplink") + 13);
			}
			else if((str.find("option downlink") != std::string::npos))
			{
				this->downlink = str.substr(str.find("option downlink") + 15);
			}
		}
	}
	in.close();
}

void openwrt::show_status()
{
	cout << "ap status" << endl;
	cout << setw(15) << left << "ssid" << this->ssid << endl;
	cout << setw(15) << left << "hwmode" << this->mode << endl;
	cout << setw(15) << left << "channel" << this->channel << endl;
	cout << setw(15) << left << "txpower" << this->tx << endl;
	cout << setw(15) << left << "password" << this->password << endl;
	cout << setw(15) << left << "uplink" << this->uplink << endl;
	cout << setw(15) << left << "downlink" << this->downlink << endl;
}

bool openwrt::set_ssid(string ssid)
{
	string str;

	if(ssid.length() > 0)
	{
		str = "/sbin/uci set wireless.@wifi-iface[0].ssid=" + ssid;
		system(str.c_str());
		return true;
	}
	else
		return false;
}

bool openwrt::set_mode(string mode)
{
	string str;
	
	if(mode == "b" || mode == "g" || mode == "a")
	{
		str = "/sbin/uci set wireless.wlan0.hwmode11" + mode;
		system(str.c_str());
		return true;
	}
	else
		return false;
}

bool openwrt::set_channel(string channel)
{
	stringstream ss;
	string str = "/sbin/uci set wireless.wlan0.channel=";
	int ch;

	if(channel != "auto")
		ch = atoi(channel.c_str());
	else
	{
		str = str + channel;
		system(str.c_str());
		return true;
	}
	
	if(0 < ch && ch < 12)
	{
		ss << str << ch;
		str = ss.str();
		system(str.c_str());
		return true;
	}
	else
		return false;
	
}

bool openwrt::set_tx(string tx)
{
	stringstream ss;
	string str = "/sbin/uci set wireless.wlan0.txpower=";
	int power = atoi(tx.c_str());

	if(0 <= power && power < 21)
	{
		ss << str << power;
		str = ss.str();
		system(str.c_str());
		return true;
	}
	else
		return false;
}

bool openwrt::set_password(string password)
{
	stringstream ss;
	string str = "/sbin/uci set wireless.@wifi-iface[0].key=" + password;
	if(8 <= password.length() && password.length() <= 63)
	{
		system(str.c_str());
		return true;
	}
	else
		return false;
}

bool openwrt::set_uplink(string uplink)
{
	stringstream ss;
	string str = "/sbin/uci set wshaper.settings.uplink=";
	int up = atoi(uplink.c_str());

	if(0 <= up)
	{
		ss << str << up;
		str = ss.str();
		system(str.c_str());
		return true;
	}
	else
		return false;
}

bool openwrt::set_downlink(string downlink)
{
	stringstream ss;
	string str = "/sbin/uci set wshaper.settings.downlink=";
	int down = atoi(downlink.c_str());

	if(0 <= down)
	{
		ss << str << down;
		str = ss.str();
		system(str.c_str());
		return true;
	}
	else
		return false;
}

string openwrt::get_ssid()
{
	return this->ssid;
}

string openwrt::get_mode()
{
	return this->mode;
}

string openwrt::get_channel()
{
	return this->channel;
}

string openwrt::get_tx()
{
	return this->tx;
}

string openwrt::get_password()
{
	return this->password;
}

string openwrt::get_uplink()
{
	return this->uplink;
}

string openwrt::get_downlink()
{
	return this->downlink;
}
