CXX=g++
CXXFLAGS=-O2 -g -Wall
CPPFLAGS=-I../ral
LDFLAGS=-L../ral -Wl,-rpath='$$ORIGIN/.'
OBJ=Estructuras.o Parser.o core.o
EXE=core

all: core

core:  $(OBJ)
	@echo "Linkeando codigos objetos del core..."
	$(CXX) -o $(EXE) $^ $(LDFLAGS) -lxerces-c -lRAL  -lpthread

%.o: %.cpp
	@echo "Compilando" $^
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) -c $^ -o $@

clean:
	@echo "Elimnando codigos objetos y ejecutable del core..."
	rm -f $(OBJ) $(EXE) *.log *.so
