// incluir las cosas estandar...
#include <fstream>
#include <iostream>
#include <signal.h>
#include <sstream>
#include "core.h"
using namespace std;

// PROTOTIPO DE FUNCIONES AUXILIARES
bool debo_terminar;
void terminar(int sig); 					// rutina de atención de SIGNAL

// MAIN !!
int main(int argc, char** argv)
{
	// chequeo los parametros
	if (argc != 3) {
		cout << "Uso: test_core ArchivoXML.xml ArchivoLOG.log" << endl;
		return 1;
	}
	
	core_initialize(argv[2]);
	
	ifstream xml_file(argv[1]);
	if (xml_file.bad())
		cerr << "No se pudo leer el XML" << endl; return 1;
	std::stringstream buffer;
	buffer << xml_file.rdbuf();
  	
	if (!core_start(buffer.str())) return 1;
	
	// meto acá el signal, después de que levanta los procesos hijos en el ral...
	signal( SIGINT, terminar ); // configurar la rutina de atención de SIGINT
	signal( SIGTERM, terminar ); // configurar la rutina de atención de SIGTERM
	
	// ejecuto
	debo_terminar = false;
	while(!debo_terminar) 
		core_execute();
	
	core_stop();
	core_deinitialize();
	
	return 0;
}

// DEFINICION DE FUNCIONES AUXILIARES
void terminar(int sig){
  debo_terminar = true;
}
