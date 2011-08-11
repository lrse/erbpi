// Interfaz RAL

#ifndef _RAL_H_
#define _RAL_H_

#include <vector>
#include <string>

#define MOTOR_00                        "motor.izquierda"
#define MOTOR_01                        "motor.derecha"

// estructura auxiliar...
struct Item {
  std::string id;
  int valor;
};

extern "C" {
	void inicializarRAL(void); 						// inicializa el robot.
	void finalizarRAL(void); 						// finaliza el robot.
	std::vector<std::string> getListaSensores(); 	// devuelve una lista de IDs de los sensores que posee el robot.
	std::vector<std::string> getListaActuadores();	// devuelve una lista de IDs de los actuadores que posee el robot.
	std::vector<Item> getEstadoSensores(); 			// devuelve una lista de <id,valor> con el nuevo estado de cada sensor.
	unsigned long getFrecuenciaTrabajo(); 			// devuelve a qué frecuencia (en microsegundos!) es posible sensar y asignarle un valor a los actuadores del robot (ciclo de control).
	void setEstadoActuadores(std::vector<Item>); 	// recibe una lista de <id;valor> con el nuevo valor para cada actuador y fija el nuevo valor de los actuadores de robot.
}

#endif // _RAL_H_
