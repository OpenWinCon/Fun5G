CPP = g++
CFLAGS = -I/usr/include/mysql
LDFLAGS = -lmysqlclient -lpthread

.SUFFIXES : .cpp .o
.cpp.o :
	$(CPP) -c $<

ALL = ap_agent

all: $(ALL)

ap_agent: ap_agent.o ../lib/ctrCommand.o ../lib/hostap.o ../lib/Report.o ../lib/Packet.o ../lib/Socket.o ../lib/APInformation.o
	$(CPP) -o $@ $? $(CLFAGS) $(LDFLAGS) 
	rm -rf *.o

clean :
	rm -rf *.o $(ALL)
