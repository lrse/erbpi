CXX=g++
CXXFLAGS=-O2 -g -Wall -I../../ral
OBJ=estructuras.o parser.o core.o

all: libcore.a

libcore.a: $(OBJ)
	@echo "Linkeando libcore..."
	ar crs libcore.a $(OBJ)
	
%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $^ -o $@

clean:
	rm -f libcore.a *.o
