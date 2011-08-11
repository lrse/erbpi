// RAL-LINUX PARA KHEPERA

#ifndef _KHEPERA_RAL_H_
#define _KHEPERA_RAL_H_

	// incluir las cosas para RS232-COM1
	#include <termios.h> 	// POSIX terminal control definitions // para configuración del puerto (Por ejemplo: "tcgetattr()" que permite salvar y configurar los atributos del puerto...)
	#include <fcntl.h>		// File control definitions // para configuración del puerto (Por ejemplo: O_RDWR, O_NOCTTY, O_NONBLOCK)

	// incluir las cosas estandar...
	#include <stdlib.h>
	#include <string.h>
		#include <stdio.h>
	#include <iostream>
	#include <vector>
	#include <string>

	// definiciones para la conexión...
	#define COM1 		"/dev/ttyS0"	// es la "direccion" del COM1 en Linux...
	#define BAUDRATE 	B9600			// BAUDRATE = 9600
	#define DATABITS_8 	CS8				// DATABITS = 8 bits
	#define STOPBITS_2 	CSTOPB			// STOPBITS_2 = 2
	#define PARITYON 	0				// es = PARITY_NONE ó PARITY_DISABLED
	#define PARITY 		0				// es = PARITY_NONE ó PARITY_DISABLED

	// definición para la frecuencia de trabajo del KHEPERA...
	// USAMOS la por defecto del YAKS que trabaja a 100 milisegundos
	#define CTR_FREC			100000

	// definiciones de los sensores del RAL para el KHEPERA
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
