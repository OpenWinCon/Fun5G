CPP = g++
CFLAGS = -I/usr/include/mysql
LDFLAGS = -lmysqlclient -lpthread

.SUFFIXES : .cpp .o
.cpp.o :
	$(CPP) -c $<

ALL = testbed 

all: $(ALL)

testbed: testbed.o ../ctrCommand.o ../hostap.o ../Report.o ../Packet.o ../Socket.o ../APInformation.o
	$(CPP) -o $@ $? $(CLFAGS) $(LDFLAGS) 
	rm -rf *.o

clean :
	rm -rf *.o $(ALL)
