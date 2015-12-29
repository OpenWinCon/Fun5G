################################################################################
# Env Variables setting
################################################################################

# STAGING_DIR
export STAGING_DIR	=	/home/ubuntu/openWRT/staging_dir

# Tool chain ver(directory name)
TOOLCHAIN			=	toolchain-arm_cortex-a7+vfp_gcc-4.8-linaro_uClibc-0.9.33.2_eabi

# binary PATH
PATH				:=	$(PATH):$(STAGING_DIR)/$(TOOLCHAIN)/bin

# header and library PATH
USR					=	$(STAGING_DIR)/$(TOOLCHAIN)/usr


################################################################################
# Option
################################################################################
CC					= arm-openwrt-linux-uclibcgnueabi-g++
INCLUDEPATH 		= ./
LIBS				= ./

################################################################################
# Source
################################################################################
SRCS				= agent.cpp
OBJS				= $(SRCS:.cpp=.o)
EXECUTABLE			= ow_agent

all: $(EXECUTABLE)

$(EXECUTABLE): $(OBJS) openwrt.o Report.o Packet.o Socket.o APInformation.o
	@echo [Link] $@ FROM: $^
	@$(CC) -o $@  $^ -L$(LIBS) -pthread

.cpp.o:
	@echo [Compile] $<
	@$(CC) -o $@ $< -c -I$(INCLUDEPATH) -D DEBUG

openwrt.o : openwrt.cpp
	@echo [Compile openwrt] $<
	@$(CC) -c openwrt.cpp

Report.o : Report.cpp
	@echo [Compile Report] $<
	@$(CC) -c Report.cpp


Packet.o : Packet.cpp
	@echo [Compile Packet] $<
	@$(CC) -c Packet.cpp

Socket.o : Socket.cpp
	@echo [Compile Socket] $<
	@$(CC) -c Socket.cpp

APInformation.o : APInformation.cpp
	@echo [Compile APInformation] $<
	@$(CC) -c APInformation.cpp

clean:
	@echo [clean]
	@rm $(OBJS) -f
	@rm $(EXECUTABLE) -f
	@rm openwrt.o
	@rm dependency -f

line:
	find ./ -type f | xargs wc -l

# http://forum.falinux.com/zbxe/?document_srl=494921&mid=lecture_tip&sort_index=regdate&order_type=desc
dep :
	@$(CC) -M -I$(INCLUDEPATH) $(SRCS) > dependency

ifeq (dependency,$(wildcard dependency))
include dependency
endif
