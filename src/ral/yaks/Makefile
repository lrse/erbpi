CXX=g++
CXXFLAGS = -fPIC -Wall -O2 -g
CPPFLAGS=-I../ 
LDFLAGS=-shared

all: libyaksRAL.so

new: clean all

yaksRAL.o: RAL.cpp
	@$(CXX) $(CXXFLAGS) $(CPPFLAGS) -c $^ -o $@

libyaksRAL.so: yaksRAL.o
	@echo "Compilando libreria libyaksRAL.so"
	@$(CXX) $(CPPFLAGS) $(LDFLAGS) $^ -o $@ 
	@cp $@ ../

clean:
	@echo "Eliminando libreria libyaksRAL.so"
	@$(RM) -f *.o *.so

