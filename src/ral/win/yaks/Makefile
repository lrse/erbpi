CC=i586-mingw32msvc-gcc
CXX=i586-mingw32msvc-g++
CFLAGS=-O2
CXXFLAGS = -fPIC -Wall -O2 -g -I../ 
LDFLAGS=-lws2_32 -shared

TARGET=./libyaksRAL.ddl

all: libyaksRAL.ddl

new: clean all

libyaksRAL.ddl: yaksRAL.o
	@echo "Compilando libreria libyaksRAL.so para Windows"
	@$(CXX) $(CXXFLAGS) $(LDFLAGS) $^ -o $@
	@cp $@ ../

clean:
	@echo "Eliminando libreria libyaksRAL.so para Windows"
	@$(RM) -f *.o *.dll

