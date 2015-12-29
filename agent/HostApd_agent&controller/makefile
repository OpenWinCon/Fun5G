CPP = g++
LDFLAGS = -lpthread

.SUFFIXES : .cpp .o
.cpp.o :
	$(CPP) -c $<

ALL = ap_agent controller command

all: $(ALL)

command: Command.o ctrCommand.o hostap.o APInformation.o
	$(CPP) -o $@ $? $(LDFLAGS) 

ap_agent: ap_agent.o ctrCommand.o hostap.o Report.o Packet.o Socket.o APInformation.o
	$(CPP) -o $@ $? $(LDFLAGS) 

controller: controller.o hostap.o Report.o Packet.o Socket.o APInformation.o dbCommand.o
	$(CPP) -o $@ $? $(LDFLAGS)
clean :
	rm -rf *.o $(ALL)

