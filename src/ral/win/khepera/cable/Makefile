CC=gcc
CXX=g++
CFLAGS=-O2
CXXFLAGS = -fPIC -Wall -O2 -g -I../..

all: libkheperaRAL.so

new: clean all

libkheperaRAL.so: kheperaRAL.o
	@echo "Compilando libreria libkheperaRAL.so para Linux"
	@$(CXX) $(CXXFLAGS) -shared -Wl $^ -o $@
	@cp $@ ../../

clean:
	@echo "Eliminando libreria libkheperaRAL.so para Linux"
	@$(RM) -f *.o *.so
