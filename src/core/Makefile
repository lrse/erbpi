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

wincore: Estructuras.o Parser.o core.o 
	# Creo el ejecutable 'core' linkeando los codigos objeto con la libreria dinamica  'RAL' y con la libreria estatica xerces-c
	$(CMGW) $(CXXFLAGS) -I../ral/win -I../lib -L../ral/win -L../lib -o core $^ -lxerces-c -Bdynamic -lRAL
	
lnxcore:
	$(CXX) $(CXXFLAGS) -I../ral/lnx -I../lib -L../ral/lnx -L../lib -o core $^ -lxerces-c -Bdynamic -lRAL

clean:
	# borro todos los archivos temporales (.o) del Core y el ejecutable del Core
	rm -f $(OBJ) $(EXE) *.log


