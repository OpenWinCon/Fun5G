CPP = g++
CFLAGS = -I/usr/include/mysql
LDFLAGS = -lmysqlclient -lpthread

.SUFFIXES : .cpp .o
.cpp.o :
	$(CPP) -c $<

ALL = Command.o ctrCommand.o hostap.o APInformation.o hostap.o Report.o Packet.o Socket.o Action.o Database.o Packet.o 

all: $(ALL)


clean :
	rm -rf *.o $(ALL)
