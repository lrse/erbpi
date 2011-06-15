# Macros
CXX=g++
CMGW=i586-mingw32msvc-g++
CXXFLAGS=-O2 -g -Wall 
OBJ=Estructuras.o Parser.o core.o
EXE=core

#WINRALFLAGS=-I../ral/win -L../ral/win/yaks 
#LNXRALFLAGS= -I../lnx/win -L../ral/lnx/yaks 
#MinGWFLAGS=-fPIC -DXML_LIBRARY 
#XERCESFLAGS= I../lib/xerces-c-src_2_8_0/include -I../lib/xerces-c-src_2_8_0/dom -L../lib/

wincore: $(OBJ)
	# Creo el ejecutable 'core' linkeando los codigos objeto con la libreria dinamica  'RAL' y con la libreria estatica xerces-c
	$(CMGW) $(CXXFLAGS) -I../ral/win -I../lib -L../ral/win -L../lib -o core $^ -lxerces-c -Bdynamic -lRAL
	
lnxcore: $(OBJ)
	# linkeo el Core con el Xerces (estático) y con el RAL (dinámico) generando el ejecutable del Core
	$(CXX) $(CXXFLAGS) -o $(EXE) -L../ral/lnx -lxerces-c -lRAL $^

# dependencias
Estructuras.o: Estructuras.cpp Estructuras.h general.h
Parser.o: Parser.cpp Parser.h Estructuras.h
core.o: core.cpp Parser.h Estructuras.h ../ral/RAL.h

clean:
	# borro todos los archivos temporales (.o) del Core y el ejecutable del Core
	rm -f $(OBJ) $(EXE) *.log


