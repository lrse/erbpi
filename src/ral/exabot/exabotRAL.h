#ifndef _EXABOT_RAL_H_
#define _EXABOT_RAL_H_

// definición para la frecuencia de trabajo del KHEPERA...
// USAMOS la por defecto del YAKS que trabaja a 100 milisegundos
#define CTR_FREC			100000

// definiciones de los sensores del RAL para el EXABOT
//#define SENSOR_00			"telemetro.180"
//#define SENSOR_01			"telemetro.270"
//#define SENSOR_02			"telemetro.315"
//#define SENSOR_03			"telemetro.0"
//#define SENSOR_04			"telemetro.45"
//#define SENSOR_05			"telemetro.90"
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

#define TELEMETER_MIN_DISTANCE 8 				//#define TELEMETER_MIN_DISTANCE 7
#define TELEMETER_MAX_DISTANCE 43 // (43=35+8) 	//#define TELEMETER_MAX_DISTANCE 35

//#define SONAR_MIN_DISTANCE 1
#define SONAR_MIN_DISTANCE 8
#define SONAR_MAX_DISTANCE 400

#endif // _RAL_H_
