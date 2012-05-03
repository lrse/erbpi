include ../../Makefile.common

CC=gcc
CXX=g++
CFLAGS=-O2
CXXFLAGS=-fPIC -Wall -O2 `pkg-config playerc++ --cflags` -I..
LDFLAGS= -shared `pkg-config --libs playerc++` 

all: libexabotPlayerRAL.so

new: clean all

libexabotPlayerRAL.so: exabotPlayerRAL.o
	$(CXX) $(CXXFLAGS) $(LDFLAGS) $^ -o $@
	cp $@ ../
	
clean:
	$(RM) -f *.o *.so
