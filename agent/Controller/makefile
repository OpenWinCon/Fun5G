CPP = g++
CFLAGS = -I/usr/include/mysql
LDFLAGS = -lmysqlclient -lpthread

.SUFFIXES : .cpp .o
.cpp.o :
	$(CPP) -c $<

ALL = controller command

all: $(ALL)

command: Command.o ../lib/ctrCommand.o ../lib/hostap.o ../lib/APInformation.o
	$(CPP) -o $@ $? $(LDFLAGS)
	rm -rf *.o

controller: controller.o ../lib/ctrCommand.o ../lib/hostap.o ../lib/Report.o ../lib/Packet.o ../lib/Socket.o ../lib/APInformation.o ../lib/Action.o ../lib/Database.o
	$(CPP) -o $@ $? $(CLFAGS) $(LDFLAGS)
	rm -rf *.o

clean :
	rm -rf *.o $(ALL)
