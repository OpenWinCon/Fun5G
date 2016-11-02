/*
 * Database.cpp
 *
 */

#include "../headers/Database.h"
#include <iomanip>
using namespace std;

Database::Database() {
	// TODO Auto-generated constructor stub
	m_MySQL = mysql_init(NULL);
	if (!m_MySQL) {
		cout << "MySQL Initialization failed";
	}
	m_MySQL = mysql_real_connect(m_MySQL, SERVER, USER, PASSWORD, DATABASE, 0, NULL, 0);
	if (m_MySQL) {
		cout << "DB Connection Succeeded\n";
	} else {
		cout << "DB Connection failed\n";
	}

	ShowTable("AP_Information");
}

Database::~Database() {
	// TODO Auto-generated destructor stub
	mysql_close(m_MySQL);
}

void Database::SendQuery(string query) {
	mysql_query(m_MySQL, query.data());

}

string Database::GetResult(string query){
	string res;
	MYSQL_RES *result;
	MYSQL_ROW row;

	mysql_query(m_MySQL, query.data());

	result = mysql_store_result(m_MySQL);
	int colnum = mysql_num_fields(result);

	while ((row = mysql_fetch_row(result)) != NULL){
		for (int i = 0; i < colnum; i++) {
			if (row[i] == NULL) {
				res.append("|");
			}
			else{
				res.append(row[i]);
				res.append("|");
			}
		}
	}
	return res;
}

void Database::ShowTable(std::string tableName){

	MYSQL_RES *result;
	MYSQL_FIELD* field;
	MYSQL_ROW row;

	mysql_query(m_MySQL, "select * from AP_Information;");

	result = mysql_store_result(m_MySQL);
	int colnum = mysql_num_fields(result);

	while ((field = mysql_fetch_field(result)) != NULL) {
		cout << setw(21) << field->name << "|";
	}
	cout << endl;

	while ((row = mysql_fetch_row(result)) != NULL){
		for (int i = 0; i < colnum; i++) {
			if (row[i] == NULL) {
				cout << setw(22) << "|";
			}
			else{
				cout << setw(21) << row[i] << "|";
			}
		}
		cout << endl;
	}
}

void Database::PrintDB() 
{
	string result = this->GetResult("select ID, IP, SSID from AP_Information;");
	string temp;

	cout << setfill('-') << setw(34) << "[Openwinnet AP LIST]" << setw(17) << "" << endl;
	cout << setfill(' ') << left << "|" << setw(19) << "ID" << setw(16) <<"IP" << setw(14) << "SSID" << "|" << endl;

	while(true)
	{
		string id, ip, ssid;
		id = result.substr(0, result.find('|'));
		result.erase(0, result.find('|')+1);

		ip = result.substr(0, result.find('|'));
		result.erase(0, result.find('|')+1);

		ssid = result.substr(0, result.find('|'));
		result.erase(0, result.find('|')+1);

		cout << "|" << setw(19) << id << setw(16) << ip << setw(14) << ssid << "|" << endl;

		if(result.length() < 1) break;
	}

	cout << setfill('-') << setw(51) << "" << endl;

}
