// ATENCIÓN: ESTE RAL ES PARA YAKS !!!!

#ifndef _YAKS_RAL_H_
#define _YAKS_RAL_H_

	// incluir las cosas estandar...
	#include <sys/types.h>
	#include <stdio.h>
	#include <stdlib.h>
	#include <string.h>
	#include <unistd.h>
	#include <vector>
	#include <string>
	
/* Incluyo la libreria para sockets de windows*/
	#include <winsock2.h>

/* Excluyo estas librerias que no funcionan en windows
 * 
 * #include <netinet/in.h>
 * #include <arpa/inet.h>
 * 
 */

	// definiciones para la conexión...
	#define SERVER_HOST			"127.0.0.1"
	#define SERVER_PORT			1033

	// definición para la frecuencia de trabajo del YAKS
	// por defecto, el YAKS trabaja a 100 milisegundos
	#define CTR_FREC			100000		

	// definiciones de los sensores del RAL para el YAKS
	#define SENSOR_00			"proximidad.320"
	#define SENSOR_01			"proximidad.340"
	#define SENSOR_02			"proximidad.350"
	#define SENSOR_03			"proximidad.10"
	#define SENSOR_04			"proximidad.20"
	#define SENSOR_05			"proximidad.40"
	#define SENSOR_06			"proximidad.170"
	#define SENSOR_07			"proximidad.190"
	#define SENSOR_08			"luz.320"
	#define SENSOR_09			"luz.340"
	#define SENSOR_10			"luz.350"
	#define SENSOR_11			"luz.10"
	#define SENSOR_12			"luz.20"
	#define SENSOR_13			"luz.40"
	#define SENSOR_14			"luz.170"
	#define SENSOR_15			"luz.190"

#endif // _RAL_H_
