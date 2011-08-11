CXXFLAGS = -fPIC -Wall -O2 -g -I..

all:	exabotRAL.so

exabotRAL.so: RAL.o teles_lineal.o
	g++ -fPIC -shared $^ -o $@
	cp $@ ../

clean:
	rm -f *.o *.so

