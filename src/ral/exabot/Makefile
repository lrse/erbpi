CXXFLAGS = -fPIC -Wall -O2 -g -I.. -I../../../../libexabot 

all:	exabotRAL.so

exabotRAL.so: RAL.o
	g++ -fPIC -shared $^ -o $@ -L../../../../libexabot  -lexabot
	cp $@ ../libexabotRAL.so

clean:
	rm -f *.o *.so

