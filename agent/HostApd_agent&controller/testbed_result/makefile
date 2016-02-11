CPP = g++
LDFLAGS = -lpthread

.SUFFIXES : .cpp .o
.cpp.o :
	$(CPP) -c $<

ALL = testbed

all: $(ALL)

testbed: testbed.o ../ctrCommand.o ../hostap.o ../Report.o ../Packet.o ../Socket.o ../APInformation.o
	$(CPP) -o $@ $? $(LDFLAGS) 
clean :
	rm -rf *.o $(ALL)

