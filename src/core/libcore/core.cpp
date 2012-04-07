// incluir las cosas estandar...
#include <fstream>
#include <iostream>
#include <signal.h>
#include <sys/timeb.h>

#include "RAL.h"
#include "estructuras.h"
#include "parser.h"

#define MILI2MICRO 1e3
#define MICRO2NANO 1e3

#define SECOND2MICRO 1e6
#define MILI2NANO 1e6

#define CLOCKS_PER_MICROSEC (CLOCKS_PER_SEC*SECOND2MICRO)

using namespace std;

// VARIABLES GLOBALES
ofstream archivo_log;                           // puntero para archivo de log...
string log_filename;

// variables para mediciones del TimeStamp y framerate
/* 
 * struct timeb {
 *   time_t         time     The seconds portion of the current time. 
 *   unsigned short millitm  The milliseconds portion of the current time. 
 *   short          timezone The local timezone in minutes west of Greenwich. 
 *   short          dstflag  TRUE if Daylight Savings Time is in effect. 
 * };
 * 
 * */
struct timeb tiempo_inicio_frame, tiempo_fin_frame, tiempo_inicio_programa;
unsigned int tiempo_diferencia, tiempo_ultimo_frame = 0;
bool primera_medicion_tiempo = true;
unsigned long frecuencia;

Conducta* conducta;

// PROTOTIPO DE FUNCIONES AUXILIARES
void terminar(int sig);                     // rutina de atención de SIGNAL
int actualizar_tiempos();

void core_initialize(const string& _log_filename)
{
    inicializarRAL();
    frecuencia = getFrecuenciaTrabajo();
    log_filename = _log_filename;
    
    init_parser();
}

bool core_start(const string& xml_filename)
{
    conducta = new Conducta;
    
    // abro el archivoLOG y verifico
    //cout << "abriendo log" << endl;
    archivo_log.open(log_filename.c_str(), ios::trunc);
    if (archivo_log.bad()) { cerr << "Core - Error: al abrir el archivo de LOG !!!" << endl; return false; }
    
    //cout << "Core - parseando XML" << endl;
    // 1. PARSEO Y LLENO TablaEjecucion
    if (parsear(xml_filename, conducta)){
        cerr << "Error: Algo no anduvo bien en el parseo del XML !!!" << endl;
        return false;
    }
    cout << "Core - Parseando... >> OK!" << endl;  

    //cout << "Core - chequeando sensores y actuadores" << endl;
    // 2. CHEQUEO QUE LOS SENSORES Y ACTUADORES COINCIDAN CON EL RAL
    if ( !conducta->ChequearSensores(getListaSensores()) )
        Error("Core - Error: Algo no anduvo bien en la definicion de sensores !!!\n");
    if ( !conducta->ChequearActuadores(getListaActuadores()) )
        Error("Core - Error: Algo no anduvo bien en la definicion de actuadores !!!\n");

    // escribo el encabezado del log...
    archivo_log << "timestamp, " << "frametime (optimo: " << frecuencia/MILI2MICRO << "ms), ";
    conducta->LoguearEncabezadoDeElementos(archivo_log);
    archivo_log << endl;

    return true;
}

void core_execute(void)
{
  // calculo los tiempos para el TimeStamp...
  ftime(&tiempo_inicio_frame);
  actualizar_tiempos();
  
  // lo ultimo se resume en esto
  const EstadoDeSensores& estado_sensores = getEstadoSensores();
  conducta->Actualizar(estado_sensores);
  
  // genero los nuevos valores de actuadores y se los seteo al RAL
  // actualizar_actuadores(tablaEjecucion);
  setEstadoActuadores(conducta->EstadoActuadores());
  
  // escribo los nuevos valores en el log...
  archivo_log << tiempo_diferencia << "ms, " << tiempo_ultimo_frame << "ms, ";
  conducta->LoguearEstadoDeElementos(archivo_log);
  archivo_log << endl;
  
  // sleep el tiempo necesario
  
  ftime(&tiempo_fin_frame);
  
  long elapsed_microseconds = MILI2MICRO * (long) ( 1000*(tiempo_fin_frame.time - tiempo_inicio_frame.time) + (tiempo_fin_frame.millitm - tiempo_inicio_frame.millitm) );

/* 
 * struct timespec { 
 *   time_t tv_sec;   // seconds 
 *   long   tv_nsec;  // nanoseconds 
 * };
 * 
 * */
  timespec sleep_ts;
  sleep_ts.tv_sec = 0;
  sleep_ts.tv_nsec = (frecuencia-elapsed_microseconds)*MICRO2NANO; // nanosegundos, frecuencia esta en micro
  nanosleep(&sleep_ts, NULL);
}

void core_stop(void)
{
  EstadoDeActuadores estadoActuadores;  
    Item itemActuador;

    cout  << endl << "Atendiendo solicitud para terminar el programa..." << endl;
    archivo_log.close();                                // cierro el archivoLOG
    
    // DETENGO LOS MOTORES !!!
    itemActuador.id = MOTOR_00;
    itemActuador.valor = 0;
    estadoActuadores.push_back(itemActuador);
    itemActuador.id = MOTOR_01;
    itemActuador.valor = 0;
    estadoActuadores.push_back(itemActuador);
    setEstadoActuadores(estadoActuadores); // envio al RAL CERO a los motores!!

    cout << endl << "Terminando..." << endl;
        
    delete conducta;
}

void core_deinitialize(void)
{
  cout << "apagando parser" << endl;
  deinit_parser();
  
  cout << "terminando RAL" << endl;
  finalizarRAL();
}

int actualizar_tiempos()
{
    if (primera_medicion_tiempo) {
        ftime(&tiempo_inicio_programa);
        primera_medicion_tiempo = false;
    }
    
    unsigned int tiempo_diferencia_nuevo = 1000.0*(tiempo_inicio_frame.time - tiempo_inicio_programa.time) + (tiempo_inicio_frame.millitm - tiempo_inicio_programa.millitm);
    tiempo_ultimo_frame = tiempo_diferencia_nuevo - tiempo_diferencia;
    tiempo_diferencia = tiempo_diferencia_nuevo;
    
    return 0;
}

