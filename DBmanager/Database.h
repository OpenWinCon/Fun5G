/*
 * Database.h
 *
 */

#ifndef DATABASE_H_
#define DATABASE_H_

#include <mysql/mysql.h>
#include <iostream>

#define SERVER "localhost"
#define USER "root"
#define PASSWORD "rhdckaktdlTdj1!"
#define DATABASE "openwinnet_temp"


class Database {
private:
	MYSQL *m_MySQL;
	int m_query_state;

public:
	Database();
	virtual ~Database();
	void SendQuery(std::string query);
	void ShowTable(std::string tableName);
	std::string GetResult(std::string query);
};

#endif /* DATABASE_H_ */
