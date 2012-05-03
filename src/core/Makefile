CXX=g++
CXXFLAGS=-O2 -g -Wall -Ilibcore -I../ral
include ../Makefile.common

LDFLAGS:=-Llibcore -L../ral -Wl,-rpath='$$ORIGIN/.'  -lcore -lpthread -lxerces-c
LDFLAGS_EXA=-L../../../libexabot -lexabot $(BOOST_LIBS)

all: core 

libcore/libcore.a:
	$(MAKE) -C libcore

core:  libcore/libcore.a core.o 
	@echo "Linkeando core..."
	$(CXX) -o core core.o $(LDFLAGS) -lRAL 
	
core-exa: libcore/libcore.a core-exa.o 
	@echo "Linkeando core-exa..."
	$(CXX) -o core-exa core-exa.o $(LDFLAGS_EXA) $(LDFLAGS) -lexabotRAL -lboost_system

%.o: %.cpp
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) -c $^ -o $@

clean:
	$(MAKE) -C libcore clean
	rm -f core core-exa *.o *.$(SHARED_EXT)
