// Interfaz RAL

#ifndef _RAL_H_
#define _RAL_H_

#include <vector>
#include <string>

#include "../core/general.h"

#define MOTOR_00                        "motor.izquierda"
#define MOTOR_01                        "motor.derecha"

extern "C" {
	void inicializarRAL(void); 								// inicializa el robot.
	void finalizarRAL(void); 								// finaliza el robot.
	const ListaDeSensores* getListaSensores(); 		// devuelve una lista de IDs de los sensores que posee el robot.
	const ListaDeActuadores* getListaActuadores();	// devuelve una lista de IDs de los actuadores que posee el robot.
	const EstadoDeSensores* getEstadoSensores();			// devuelve una lista de <id,valor> con el nuevo estado de cada sensor.
	unsigned long getFrecuenciaTrabajo(); 					// devuelve a qué frecuencia (en microsegundos!) es posible sensar y asignarle un valor a los actuadores del robot (ciclo de control).
	void setEstadoActuadores(const EstadoDeActuadores*);	// recibe una lista de <id;valor> con el nuevo valor para cada actuador y fija el nuevo valor de los actuadores de robot.
}

#endif // _RAL_H_
