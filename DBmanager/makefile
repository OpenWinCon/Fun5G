CPP = g++
CFLAGS = -I/usr/include/mysql
LDFLAGS = -lmysqlclient -lpthread

.SUFFIXES : .cpp .o
.cpp.o :
	$(CPP) -c $< $(CFLAGS)

ALL = dbManager 

all: $(ALL)

dbManager: main.o Action.o Database.o Packet.o Socket.o
	$(CPP) -o $@ $? $(CFLAGS) $(LDFLAGS) 
clean :
	rm -rf *.o $(ALL)

