#include <iostream>
#include <cmath>
#include <cstdlib>
#include <list>
#include <vector>
#include <sys/types.h>
#include <RAL.h>
#include <libexabot.h>
#include "exabotRAL.h"
using namespace std;

exa_sensors sensor_data;

extern "C" {
  
void inicializarRAL(void) {
  exa_initialize();
  exa_zero_data(&sensor_data);
  
  exa_set_sensor(true, SENSOR_012);    usleep(600000);
  exa_set_sensor(true, SENSOR_345);    usleep(600000);
  exa_set_sensor(true, SENSOR_67);     usleep(600000);
  exa_set_sensor(true, SENSOR_SONAR);  usleep(300000);
  exa_set_sensor(true, SENSOR_LF);     usleep(300000);
  exa_set_sensor(true, SENSOR_BUMPER); usleep(300000);
}

void finalizarRAL(void) {
  exa_set_sensor(false, SENSOR_012);    usleep(200000);
  exa_set_sensor(false, SENSOR_345);    usleep(200000);
  exa_set_sensor(false, SENSOR_67);     usleep(200000);
  exa_set_sensor(false, SENSOR_SONAR);  usleep(200000);
  exa_set_sensor(false, SENSOR_LF);     usleep(200000);
  exa_set_sensor(false, SENSOR_BUMPER); usleep(200000);
  exa_set_motors(0,0); usleep(200000);
  
  exa_deinitialize();
}

std::vector<std::string> getListaSensores(void) {
  std::vector<std::string> sensors(16);
	sensors[0] = SENSOR_00;
	sensors[1] = SENSOR_01;
	sensors[2] = SENSOR_02;
	sensors[3] = SENSOR_03;
	sensors[4] = SENSOR_04;
	sensors[5] = SENSOR_05;
	sensors[6] = SENSOR_06;
	sensors[7] = SENSOR_07;
	sensors[8] = SENSOR_08;
	sensors[9] = SENSOR_09;
	sensors[10] = SENSOR_10;
	return sensors;
}

std::vector<std::string> getListaActuadores(void) {
	std::vector<std::string> motors(2);
	motors[0] = MOTOR_00;
	motors[1] = MOTOR_01;
	return motors;
}

std::vector<Item> getEstadoSensores(void) {
  exa_receive(&sensor_data);
  cout << "recibiendo" << endl;
  float distances[8];
  
  
  cout << "sensor_data[8] = [ " ;
  for (int i = 0; i < 8; i++) {
    cout << sensor_data.telemeters[i] << " ; " ;
  }
  cout << " ]" << endl;
  
    
  exa_telemeter_distances(&sensor_data, distances);
  
  Item item;
	std::vector<std::string> sensorsName = getListaSensores();
	std::vector<Item> sensors;
  
  for (uint i = 0; i < 6; i++) {
    item.id = sensorsName[i];
    float d = distances[i];
    cout << "d: " << d << endl;
    if (d < TELEMETER_MIN_DISTANCE)
      item.valor = 100;
    else if (d > TELEMETER_MAX_DISTANCE)
      item.valor = 0;
    else {
      float normalized = (d - TELEMETER_MIN_DISTANCE) / (TELEMETER_MAX_DISTANCE - TELEMETER_MIN_DISTANCE);
      item.valor = (unsigned int)floorf((1 - normalized) * 100);
    }
    cout << "tele " << item.id << " " << item.valor << endl;
    sensors.push_back(item);
  }
  
  item.id = sensorsName[6];
  float sonar_distance = exa_sonar_distance(&sensor_data);
  if (sonar_distance < SONAR_MIN_DISTANCE) item.valor = 100;
  else if (sonar_distance > SONAR_MAX_DISTANCE) item.valor = 0;
  else
    item.valor = (unsigned int)((1.0 - ((sonar_distance - SONAR_MIN_DISTANCE) / (float)(SONAR_MAX_DISTANCE - SONAR_MIN_DISTANCE))) * 100);
  sensors.push_back(item);    
  
  cout << "distancia sonar: " << sonar_distance << " activacion: " << item.valor << endl;
	
  for (uint i = 0; i < 2; i++) {
    item.id = sensorsName[7 + i];
    //item.valor = (sensor_data.linefollowing[i] == 255 ? 100 : 0);
    item.valor = (sensor_data.linefollowing[i] == 255 ? 0 : 100);
    sensors.push_back(item);
  }
  
  for (uint i = 0; i < 2; i++) {
    item.id = sensorsName[9 + i];
    item.valor = (sensor_data.bumpers[i] == 255 ? 100 : 0);
    sensors.push_back(item);
  }

	return sensors;
}

unsigned long getFrecuenciaTrabajo(void) {
  return CTR_FREC;
}

#define MOTOR_MAX_RANGE 30
#define MOTOR_SATURATE_LOW 3 
#define MOTOR_SATURATE_HIGH 20 

void setEstadoActuadores(std::vector<Item> actuators)
{
  int valor_motores[2];
  cout << "input: " << actuators[1].valor << " " << actuators[0].valor << endl;
  for (uint i = 0; i < 2; i++) {
    float normalizado = (float)actuators[i].valor / 100.0;
    valor_motores[i] = (int)floorf(normalizado * MOTOR_MAX_RANGE);
    
    if (abs(valor_motores[i]) < MOTOR_SATURATE_LOW) valor_motores[i] = 0;
    else if (abs(valor_motores[i]) > MOTOR_SATURATE_HIGH)
      valor_motores[i] = (valor_motores[i] < 0 ? -1 : 1) * MOTOR_SATURATE_HIGH;
  }
 
  //cout << "seteando: " << (int)valor_motores[1] << " " << (int)valor_motores[0] << endl;
  //exa_set_motors(valor_motores[1], valor_motores[0]);
  exa_set_motors(valor_motores[0], valor_motores[1]);
}
}
