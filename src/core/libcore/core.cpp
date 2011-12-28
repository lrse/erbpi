// incluir las cosas estandar...
#include <fstream>
#include <iostream>
#include <signal.h>
#include <sys/timeb.h>

#include "RAL.h"
#include "estructuras.h"
#include "parser.h"

using namespace std;

// VARIABLES GLOBALES
ofstream archivo_log; 						// puntero para archivo de log...
string log_filename;
struct timeb tiempo_inicio, tiempo_actual; 	// variables para mediciones del TimeStamp
int tiempo_diferencia; 						// variables para mediciones del TimeStamp
bool primera_medicion_tiempo = true;		// variables para mediciones del TimeStamp
unsigned long frecuencia;
Conducta* conducta;

// PROTOTIPO DE FUNCIONES AUXILIARES
void terminar(int sig); 					// rutina de atención de SIGNAL
int actualizar_tiempos();

void core_initialize(const string& _log_filename) {
  inicializarRAL();
	frecuencia = getFrecuenciaTrabajo();
  log_filename = _log_filename;
  
  init_parser();
}

bool core_start(const string& xml_filename)
{
  conducta = new Conducta;
  
  // abro el archivoLOG y verifico
  cout << "abriendo log" << endl;
	archivo_log.open(log_filename.c_str(), ios::trunc);
	if (archivo_log.bad()) { cerr << "Error: al abrir el archivo del LOG !!!" << endl; return false; }
  
  cout << "parseando XML" << endl;
  // 1. PARSEO Y LLENO TablaEjecucion
	if (parsear(xml_filename, conducta)){
		cerr << "Error: Algo no anduvo bien en el parseo del XML !!!" << endl;
		return false;
	}

  cout << "chequeando sensores y actuadores" << endl;
	// 2. CHEQUEO QUE LOS SENSORES Y ACTUADORES COINCIDAN CON EL RAL
  // 2. CHEQUEO QUE LOS SENSORES Y ACTUADORES COINCIDAN CON EL RAL
	if ( !conducta->ChequearSensores(getListaSensores()) )
		Error("Error: Algo no anduvo bien en la definicion de sensores !!!\n");
	if ( !conducta->ChequearActuadores(getListaActuadores()) )
		Error("Error: Algo no anduvo bien en la definicion de actuadores !!!\n");

	// escribo el encabezado del log...
  archivo_log << "timestamp, ";
	conducta->LoguearEncabezadoDeElementos(archivo_log);
	archivo_log << endl;

	return true;
}

void core_execute(void)
{
  // calculo los tiempos para el TimeStamp...
	actualizar_tiempos();
  
  // lo ultimo se resume en esto
  const EstadoDeSensores& estado_sensores = getEstadoSensores();
  conducta->Actualizar(estado_sensores);
  
  // genero los nuevos valores de actuadores y se los seteo al RAL
  // actualizar_actuadores(tablaEjecucion);
  setEstadoActuadores(conducta->EstadoActuadores());
  
  // escribo los nuevos valores en el log...
  archivo_log << tiempo_diferencia << ", ";
  conducta->LoguearEstadoDeElementos(archivo_log);
  archivo_log << endl;
      
	// sleep el tiempo necesario
  timespec sleep_ts, sleep_rem;
  sleep_ts.tv_sec = 0;
  sleep_ts.tv_nsec = frecuencia * 1000; // nanosegundos, frecuencia esta en micro
  nanosleep(&sleep_ts, &sleep_rem);
}

void core_stop(void)
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
	setEstadoActuadores(estadoActuadores); // envio al RAL CERO a los motores!!

	cout << endl << "Terminando..." << endl;
		
	finalizarRAL(); 	// FINALIZO EL RAL...
  
	delete conducta;
}

void core_deinitialize(void) {
  cout << "apagando parser" << endl;
  deinit_parser();
  
  cout << "terminando RAL" << endl;
  finalizarRAL();
}

int actualizar_tiempos(){
	if (primera_medicion_tiempo){
		ftime(&tiempo_inicio);
		primera_medicion_tiempo = false;
	}
	ftime(&tiempo_actual);
	tiempo_diferencia = (int) (1000.0 * (tiempo_actual.time - tiempo_inicio.time) + (tiempo_actual.millitm - tiempo_inicio.millitm));
	return 0;
}
