// RAL-LINUX PARA EXABOT

// las posiciones y asignaciones de los telémtros
// pueden verse correctamente en "exabot_telemetros_posiciones_02.png"
// que se encuentra adjunto a este RAL.h

#ifndef _EXABOT_RAL_H_
#define _EXABOT_RAL_H_

	// incluir las librerías que vamos a usar
	#include <sys/shm.h>
	#include <errno.h>
	#include <time.h>
	#include <signal.h>
	#include <sys/types.h>
	#include <sys/socket.h>
	#include <netinet/in.h>
	#include <arpa/inet.h>
	#include <stdio.h>
	#include <stdlib.h>
	#include <string.h>
	#include <unistd.h>
	#include <vector>
	#include <string>

	// definición para la conexión por UDP...
	#define IP_EXA_CABLE			0xC0A80032 // por cable es 192.168.0.50
	#define IP_EXA_WIFI				0xC0A80132 // por WiFi  es 192.168.1.50
	#define IP_EXA					0xC0A80132 // la que usamos...
	// hay que cambiar esta última por ahora para definir por dónde nos queremos comunicar...


	// definición para la frecuencia de trabajo del KHEPERA...
	// USAMOS la por defecto del YAKS que trabaja a 100 milisegundos
	#define CTR_FREC			100000

	// definiciones de los sensores del RAL para el EXABOT
	#define SENSOR_00			"telemetro.270"
	#define SENSOR_01			"telemetro.315"
	#define SENSOR_02			"telemetro.0"
	#define SENSOR_03			"telemetro.45"
	#define SENSOR_04			"telemetro.90"
	#define SENSOR_05			"telemetro.180"
	#define SENSOR_06			"sonar.0"
	#define SENSOR_07			"linea.0"
	#define SENSOR_08			"linea.1"
	#define SENSOR_09			"contacto.0"
	#define SENSOR_10			"contacto.1"
	
	// esta es para los telémetros...
	#define USLEEP_TIME 100000 //10 veces por segundo
	//#define USLEEP_TIME 1000000 //1 veces por segundo

#endif // _RAL_H_
