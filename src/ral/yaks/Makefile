include ../../Makefile.common

CXX=g++
CXXFLAGS = -fPIC -Wall -O2 -g
CPPFLAGS=-I../ 
LDFLAGS:=$(BOOST_LIBS)

LIB:= libyaksRAL.$(SHARED_EXT)

all: $(LIB)

new: clean all

$(LIB): RAL.o
	$(CXX) $(CPPFLAGS)  -o $@ -shared   $^  $(LDFLAGS)
	cp $@ ../

clean:
	$(RM) -f *.o *.$(SHARED_EXT)

