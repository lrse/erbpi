CC=gcc
CXX=g++
CFLAGS=-O2
CXXFLAGS = -fPIC -Wall -O2 -g -I../
LDFLAGS=-shared

TARGET=./libyaksRAL.so

all: libyaksRAL.so

new: clean all

libyaksRAL.so: yaksRAL.o
	@echo "Compilando libreria libyaksRAL.so para Linux"
	@$(CXX) $(CXXFLAGS) $(LDFLAGS) $^ -o $@
	@cp $@ ../

clean:
	@echo "Eliminando libreria libyaksRAL.so para Linux"
	@$(RM) -f *.o *.so

