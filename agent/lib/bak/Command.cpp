#include "ctrCommand.h"
#include <iostream>

using namespace std;



int main(int argc, char * argv[])
{
	if(argc < 2)
	{
		cout << "[E] Usage: " << argv[0] << " <IP> <Command> [value] " << endl;
	}
	string cand;
	cand.assign(argv[1]);

	char request[1024];

	if(argc > 3)
	{
		sprintf(request, "%s", argv[2]);
		for(int i = 3; i < argc; i++)
		{
			strcat(request, " ");
			strcat(request, argv[i]);
		}
		cout << request << endl;
		ctrCommand cmd(cand, request);

	}
	else 
	{	
		ctrCommand cmd(cand, argv[2]);
	}

	cout << "[P] process Command" << endl;
	return 0;
}
