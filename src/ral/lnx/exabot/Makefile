CC=gcc
CXX=g++
CFLAGS=-O2
CXXFLAGS = -fPIC -Wall -O2 -g -I../
LDFLAGS=-shared

all: libexabotRAL.so

new: clean all

libexabotRAL.so: exabotRAL.o teles_lineal.o
	@echo "Compilando libreria libexabotRAL.so para Linux"
	@$(CXX) $(CXXFLAGS) $(LDFLAGS) $^ -o $@
	@cp $@ ../

clean:
	@echo "Eliminando libreria libexabotRAL.so para Linux"
	@$(RM) -f *.o *.so

