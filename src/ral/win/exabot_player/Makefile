CC=gcc
CXX=g++
CFLAGS=-O2
CXXFLAGS=-fPIC -Wall -O2 `pkg-config playerc++ --cflags` -I..
LDFLAGS=`pkg-config --libs playerc++`

all: libexabotPlayerRAL.so

new: clean all

libexabotPlayerRAL.so: exabotPlayerRAL.o
	@echo "Compilando libreria libexabotPlayerRAL.so para Linux"
	@$(CXX) $(CXXFLAGS) $(LDFLAGS) -shared $^ -o $@
	@cp $@ ../
	
clean:
	@echo "Eliminando libreria libexabotPlayerRAL.so para Linux"
	@$(RM) -f *.o *.so
