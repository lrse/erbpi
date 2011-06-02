// incluir las cosas estandar...
#include <fstream>
#include <iostream>
#include <signal.h>
#include <sys/timeb.h>
// incluir las Estructuras que utiliza el Core
#include "Estructuras.h"
// incluir el parser
#include "Parser.h"
// incluir el RAL
#include <RAL.h>
using namespace std;

// VARIABLES GLOBALES
ofstream archivo_log; 						// puntero para archivo de log...
struct timeb tiempo_inicio, tiempo_actual; 	// variables para mediciones del TimeStamp
int tiempo_diferencia; 						// variables para mediciones del TimeStamp
bool primera_medicion_tiempo = true;		// variables para mediciones del TimeStamp
vector<Elemento*> tablaEjecucion; 			// creo mi vector elementos "TablaEjecucion"...

// PROTOTIPO DE FUNCIONES AUXILIARES
void terminar(int sig); 					// rutina de atención de SIGNAL
int chequeo_sensores_y_actuadores(vector<Elemento*>&);
int actualizar_sensores(vector<Elemento*>&);
int actualizar_actuadores(vector<Elemento*>&);
int actualizar_tiempos();
int escribir_log_encabezado(vector<Elemento*>&);
int escribir_log_valores(vector<Elemento*>&);


// MAIN !!
int main(int argc, char** argv){
	
	int i, j;
	unsigned long frecuencia;
	string archivoXML; // archivo a parsear...
	char* archivoLOG; // archivo de Log...
	string RAL_ID; // identificacion de RAL...

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

	// 1. PARSEO Y LLENO TablaEjecucion
	if ( parsear(archivoXML, tablaEjecucion) ){
		cout << "Error: Algo no anduvo bien en el parseo del XML !!!" << endl;
		return 1;
	}

	// 2. CHEQUEO QUE LOS SENSORES Y ACTUADORES COINCIDAN CON EL RAL
	if ( chequeo_sensores_y_actuadores(tablaEjecucion) ){
		cout << "Error: Algo no anduvo bien en la definicion de sensores y actuadores !!!" << endl;
		return 1;
	}
	
	// 3. OBTENGO LA FRECUENCIA DE TRABAJO DEL RAL
	frecuencia = getFrecuenciaTrabajo();

	// 4. EJECUTO MIENTRAS LA FRECUENCIA LO PERMITA	
	
	// escribo el encabezado del log...
	escribir_log_encabezado(tablaEjecucion);
	
	// ejecuto
	while (1){
		
		// calculo los tiempos para el TimeStamp...
		actualizar_tiempos();
		
		// actualizo el nuevo valor de cada sensor en TablaEjecucion...
		actualizar_sensores(tablaEjecucion);

		// ejecuto todos en tablaEjecucion
		for (i = 0; i < tablaEjecucion.size(); i++)
			(*(tablaEjecucion[i])).ejecutar();

		// genero los nuevos valores de actuadores y se los seteo al RAL
		actualizar_actuadores(tablaEjecucion);

		// escribo los nuevos valores en el log...
		escribir_log_valores(tablaEjecucion);

		// sincronizar la frecuencia del Core con el RAL... // USLEEP RECIBE MICRO-SEGUNDOS (10^-6)
		usleep( frecuencia ); // si frecuencia = 100000 => ejecuta 10 veces x seg... porque está en micro-seg
	}

	// cierro el archivoLOG
	archivo_log.close();

	// destruyo todo...
	for (int i = 0; i < tablaEjecucion.size(); i++)
		delete (tablaEjecucion[i]);

	// FINALIZO EL RAL...
	finalizarRAL();

	return 0;
}








// DEFINICION DE FUNCIONES AUXILIARES
void terminar(int sig){
	vector<Item> estadoActuadores;	
	Item itemActuador;

	cout  << endl << "Atendiendo solicitud para terminar el programa..." << endl;
	archivo_log.close(); 								// cierro el archivoLOG
	for (int i = 0; i < tablaEjecucion.size(); i++) 	// destruyo toda la tabla de ejecución...
		delete (tablaEjecucion[i]);
	
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
	exit(sig); 			// termino con la señal recibida...
}

int chequeo_sensores_y_actuadores(vector<Elemento*>& tabla){
	int i = 0;
	int j = 0;
	vector<string> listaSensores = getListaSensores(); // recibo del RAL la lista de sensores
	vector<string> listaActuadores = getListaActuadores(); // recibo del RAL la lista de actuadores
	for (i = 0; i < tabla.size(); i++){
		if ( (*(tabla[i]))._tipo == TIPO_SENSOR )
			for (j = 0; j < listaSensores.size(); j++)
				if ( listaSensores[j] == (*(tabla[i])).getId() )
					break;
		if ( j == listaSensores.size() ){
			cout << "Error: los sensores no se corresponden !!!" << endl;
			return 1;
		}
	}
	for (i = 0; i < tabla.size(); i++){
		if ( (*(tabla[i]))._tipo == TIPO_ACTUADOR )
			for (j = 0; j < listaActuadores.size(); j++)
				if ( listaActuadores[j] == (*(tabla[i])).getId() )
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
			if ( estadoSensores[i].id == (*(tabla[j])).getId() ){
				(*(tabla[j])).setValor( estadoSensores[i].valor );
				break;
			}
	return 0;
}

int actualizar_actuadores(vector<Elemento*>& tabla){
	int i = 0;
	vector<Item> estadoActuadores;	
	Item itemActuador;
	for (i = 0; i < tabla.size(); i++)
		if ( (*(tabla[i]))._tipo == TIPO_ACTUADOR ){
			itemActuador.id = (*(tabla[i])).getId();
			itemActuador.valor = (*(tabla[i])).getValor();
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
