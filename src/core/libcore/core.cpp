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
vector<Elemento*> tablaEjecucion; 			// creo mi vector elementos "TablaEjecucion"...
unsigned long frecuencia;

// PROTOTIPO DE FUNCIONES AUXILIARES
void terminar(int sig); 					// rutina de atención de SIGNAL
int chequeo_sensores_y_actuadores(vector<Elemento*>&);
int actualizar_sensores(vector<Elemento*>&);
int actualizar_actuadores(vector<Elemento*>&);
int actualizar_tiempos();
int escribir_log_encabezado(vector<Elemento*>&);
int escribir_log_valores(vector<Elemento*>&);

void core_initialize(const string& _log_filename) {
  inicializarRAL();
	frecuencia = getFrecuenciaTrabajo();
  log_filename = _log_filename;
  
  init_parser();
}

bool core_start(const string& xml_filename)
{
  // abro el archivoLOG y verifico
  cout << "abriendo log" << endl;
	archivo_log.open(log_filename.c_str(), ios::trunc);
	if (archivo_log.bad()) { cerr << "Error: al abrir el archivo del LOG !!!" << endl; return false; }
  
  cout << "parseando XML" << endl;
  // 1. PARSEO Y LLENO TablaEjecucion
	if (parsear(xml_filename, tablaEjecucion)){
		cerr << "Error: Algo no anduvo bien en el parseo del XML !!!" << endl;
		return false;
	}

  cout << "chequeando sensores y actuadores" << endl;
	// 2. CHEQUEO QUE LOS SENSORES Y ACTUADORES COINCIDAN CON EL RAL
	if (chequeo_sensores_y_actuadores(tablaEjecucion)){
		cerr << "Error: Algo no anduvo bien en la definicion de sensores y actuadores !!!" << endl;
		return false;
	}	

	// escribo el encabezado del log...
	escribir_log_encabezado(tablaEjecucion);

	return true;
}

void core_execute(void)
{
  // calculo los tiempos para el TimeStamp...
	actualizar_tiempos();
		
	// actualizo el nuevo valor de cada sensor en TablaEjecucion...
	actualizar_sensores(tablaEjecucion);

	// ejecuto todos en tablaEjecucion
	for (int i = 0; i < tablaEjecucion.size(); i++)
		tablaEjecucion[i]->ejecutar();

	// genero los nuevos valores de actuadores y se los seteo al RAL
	actualizar_actuadores(tablaEjecucion);

	// escribo los nuevos valores en el log...
	escribir_log_valores(tablaEjecucion);

	// sleep el tiempo necesario
  timespec sleep_ts, sleep_rem;
  sleep_ts.tv_sec = 0;
  sleep_ts.tv_nsec = frecuencia * 1000; // nanosegundos, frecuencia esta en micro
  nanosleep(&sleep_ts, &sleep_rem);
}

void core_stop(void)
{
	archivo_log.close(); 								// cierro el archivoLOG
  
	for (int i = 0; i < tablaEjecucion.size(); i++) 	// destruyo toda la tabla de ejecución...
		delete tablaEjecucion[i];
  tablaEjecucion.clear();
	
	// DETENGO LOS MOTORES !!!
  vector<Item> estadoActuadores;	
	Item itemActuador;  
	itemActuador.id = MOTOR_00;
	itemActuador.valor = 0;
	estadoActuadores.push_back(itemActuador);
	itemActuador.id = MOTOR_01;
	itemActuador.valor = 0;
	estadoActuadores.push_back(itemActuador);
	setEstadoActuadores(estadoActuadores); // envio al RAL CERO a los motores!!
}

void core_deinitialize(void) {
  cout << "apagando parser" << endl;
  deinit_parser();
  
  cout << "terminando RAL" << endl;
  finalizarRAL();
}

int chequeo_sensores_y_actuadores(vector<Elemento*>& tabla){
	int i = 0;
	int j = 0;
	vector<string> listaSensores = getListaSensores(); // recibo del RAL la lista de sensores
	vector<string> listaActuadores = getListaActuadores(); // recibo del RAL la lista de actuadores
	for (i = 0; i < tabla.size(); i++){
		if ( tabla[i]->_tipo == TIPO_SENSOR )
			for (j = 0; j < listaSensores.size(); j++)
				if ( listaSensores[j] == tabla[i]->getId() )
					break;
		if ( j == listaSensores.size() ){
			cout << "Error: los sensores no se corresponden !!!" << endl;
			return 1;
		}
	}
	for (i = 0; i < tabla.size(); i++){
		if ( tabla[i]->_tipo == TIPO_ACTUADOR )
			for (j = 0; j < listaActuadores.size(); j++)
				if ( listaActuadores[j] == tabla[i]->getId() )
					break;
		if ( j == listaActuadores.size() ){
			cout << "Error: los actuadores no se corresponden !!!" << endl;
			return 1;
		}
	}
	return 0;
}

int actualizar_sensores(vector<Elemento*>& tabla){
	// se asume que los sensores se corresponden...
	int i = 0;
	int j = 0;
	vector<Item> estadoSensores = getEstadoSensores(); // recibo del RAL el nuevo estado de sensores
	for (i = 0; i < estadoSensores.size(); i++)
		for (j = 0; j < tabla.size(); j++)
			if ( estadoSensores[i].id == tabla[j]->getId() ){
				tabla[j]->setValor( estadoSensores[i].valor );
				break;
			}
	return 0;
}

int actualizar_actuadores(vector<Elemento*>& tabla){
	int i = 0;
	vector<Item> estadoActuadores;	
	Item itemActuador;
	for (i = 0; i < tabla.size(); i++)
		if ( tabla[i]->_tipo == TIPO_ACTUADOR ){
			itemActuador.id = tabla[i]->getId();
			itemActuador.valor = tabla[i]->getValor();
			estadoActuadores.push_back(itemActuador);
		}
	setEstadoActuadores(estadoActuadores); // envio al RAL el nuevo estado de actuadores
	return 0;
}

int escribir_log_encabezado(vector<Elemento*>& tabla){
	int i = 0;
	archivo_log << "timestamp, "; 	// el primer valor es el timestamp...
	for (i = 0; i < tabla.size(); i++)
		archivo_log << (*(tabla[i])).getId() << ", ";
	archivo_log << endl ;
	return 0;
}

int escribir_log_valores(vector<Elemento*>& tabla){
	int i = 0;
	archivo_log << tiempo_diferencia << ", "; // tiempo_diferencia está en milisegundos
	for (i = 0; i < tabla.size(); i++){
		if ( (*(tabla[i]))._tipo == TIPO_CAJA )
			archivo_log << (*(tabla[i])).getEntrada() << ", " << (*(tabla[i])).getValor() << ", ";
		else archivo_log << (*(tabla[i])).getValor() << ", ";
	}
	archivo_log << endl;
	return 0;
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
