
#include <fstream>
#include <iostream>
#include <signal.h>
#include <sys/timeb.h>
using namespace std;

#include "Estructuras.h"
#include "Parser.h"
#include "../ral/RAL.h"

// VARIABLES GLOBALES
ofstream archivo_log;			// puntero para archivo de log...

// PROTOTIPOS DE FUNCIONES AUXILIARES
void terminar(int sig);			// rutina de atención de SIGNAL

int main(int argc, char** argv)
{
	unsigned long frecuencia;
	string archivoXML;			// archivo a parsear...
	char* archivoLOG;			// archivo de Log...
	string RAL_ID;				// identificacion de RAL...

	// chequeo los parametros
	if ( argc != 4 ){
		cout << "Debe especificar el archivo a parsear, el archivo para el LOG y la especificación para el RAL." << endl << endl;
		cout << "Por ejemplo: ./test_core ArchivoXML.xml ArchivoLOG.log RAL_ID" << endl << endl;
		cout << "\t donde RAL_ID podría ser: exabot, khepera, yaks, etc." << endl << endl;
		return 1;
	}
		
	archivoXML = argv[1];	// mi archivo a parsear...
	archivoLOG = argv[2];	// mi archivo para LOG...
	RAL_ID = argv[3];		// mi definición de RAL...

	/*********************************************************
		EL "INICIALIZAR_RAL" LO PONEMOS LO MÁS ARRIBA POSIBLE
		PARA QUE CUANDO HACE LOS 2 FORK, COMPARTA LO MENOS 
		POSIBLE DE MEMORIA COMPARTIDA CON EL PROCESO
		PADRE (Core), COMO FILEDESCRIPTORS, ETC... DE LO 
		CONTRARIO, PUEDEN SUCEDER CAGADAS...

		POR ESO MISMO, TAMBIÉN PUSIMOS EL "SIGNAL" DESPUES
		DEL "INICIALIZAR_RAL", PARA QUE NO ESTÉ COMPARTIDO
		ENTRE LOS 3 PROCESOS...
		SI NO, PASABA QUE AL TOCAR "Ctrl+C" ATENDIA LA SEÑAL
		PARA TRES PROCESOS, CORE+UDP_SEND+UDP_RECEIVE...
	*********************************************************/
	// 3/2. INICIALIZO EL RAL...
	inicializarRAL();
	
	// meto acá el signal, después de que levanta los procesos hijos en el ral...
	signal( SIGINT, terminar ); // configurar la rutina de atención de SIGINT
	signal( SIGTERM, terminar ); // configurar la rutina de atención de SIGTERM

	// abro el archivoLOG y verifico
	archivo_log.open ( archivoLOG, ios::trunc );
	if ( archivo_log.bad() ){
		cout << "Error: al abrir el archivo del LOG !!!" << endl;
		return 1;
	}
	
	Conducta conducta(archivo_log);
	
	// 1. PARSEO Y LLENO TablaEjecucion
	if ( parsear(archivoXML, &conducta) ){
		cout << "Error: Algo no anduvo bien en el parseo del XML !!!" << endl;
		return 1;
	}
	
	// 2. CHEQUEO QUE LOS SENSORES Y ACTUADORES COINCIDAN CON EL RAL
	if ( !conducta.ChequearSensores(getListaSensores()) )
		Error("Error: Algo no anduvo bien en la definicion de sensores !!!\n");
	if ( !conducta.ChequearActuadores(getListaActuadores()) )
		Error("Error: Algo no anduvo bien en la definicion de actuadores !!!\n");
	
	// 3. OBTENGO LA FRECUENCIA DE TRABAJO DEL RAL
	frecuencia = getFrecuenciaTrabajo();

	// 4. EJECUTO MIENTRAS LA FRECUENCIA LO PERMITA	
	
	// ejecuto
	while (1){
		
		const EstadoDeSensores* estado_sensores = getEstadoSensores();
		conducta.Actualizar(estado_sensores);
		delete estado_sensores;
		
		// genero los nuevos valores de actuadores y se los seteo al RAL
		// actualizar_actuadores(tablaEjecucion);
		const EstadoDeActuadores* estado_actuadores = conducta.EstadoActuadores();
		setEstadoActuadores(estado_actuadores);
		delete estado_actuadores;
		
		// TODO: preguntarle a javi si aca no hay que restarle lo que trado el ciclo
		// sincronizar la frecuencia del Core con el RAL... // USLEEP RECIBE MICRO-SEGUNDOS (10^-6)
		usleep( frecuencia ); // si frecuencia = 100000 => ejecuta 10 veces x seg... porque está en micro-seg
	}

	// cierro el archivoLOG
	archivo_log.close();

	// FINALIZO EL RAL...
	finalizarRAL();

	return 0;
}

// DEFINICION DE FUNCIONES AUXILIARES
void terminar(int sig)
{
	EstadoDeActuadores estadoActuadores;	
	Item itemActuador;

	cout  << endl << "Atendiendo solicitud para terminar el programa..." << endl;
	archivo_log.close(); 								// cierro el archivoLOG
	
	// DETENGO LOS MOTORES !!!
	itemActuador.id = MOTOR_00;
	itemActuador.valor = 0;
	estadoActuadores.push_back(itemActuador);
	itemActuador.id = MOTOR_01;
	itemActuador.valor = 0;
	estadoActuadores.push_back(itemActuador);
	setEstadoActuadores(&estadoActuadores); // envio al RAL CERO a los motores!!

	cout << endl << "Terminando..." << endl;
		
	finalizarRAL(); 	// FINALIZO EL RAL...
	exit(sig); 			// termino con la señal recibida...
}

