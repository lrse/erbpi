include ../../../Makefile.common

CXX=g++
CXXFLAGS=-fPIC -Wall -O2 -g
CPPFLAGS=-I../..
LDFLAGS=-shared

all: libkheperaRAL.so

new: clean all

kheperaRAL.o: RAL.cpp
	@$(CXX) $(CXXFLAGS) $(CPPFLAGS) -c $^ -o $@

libkheperaRAL.so: kheperaRAL.o
	@echo "Compilando libreria libkheperaRAL.so"
	@$(CXX) $(CXXFLAGS) $(CPPFLAGS) $(LDFLAGS) -Wl $^ -o $@
	@cp $@ ../../

clean:
	@echo "Eliminando libreria libkheperaRAL.so"
	@$(RM) -f *.o *.so
