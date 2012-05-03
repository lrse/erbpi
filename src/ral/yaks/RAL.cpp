#define BOOST_SYSTEM_USE_LIB
#define BOOST_THREAD_USE_LIB
#include <boost/array.hpp>
#include <boost/asio.hpp>
#include <boost/algorithm/string.hpp>
#include <vector>
#include <RAL.h>
#include "yaksRAL.h"

/*** DEFINICION DE VARIABLES DE NORMALIZACION DE SENSORES Y MOTORES ****/
#define sensorProxMax				1023
#define sensorProxMin				0
#define sensorLightMax				512
#define sensorLightMin				0
#define sensorMaxNormalizado		100
#define sensorMinNormalizado		0
#define motorMax					100
#define motorMin					-100
#define motorMaxDesnormalizado		20
#define motorMinDesnormalizado		-20
// estos son porque el yaks satura los valores de los motores en estos valores...
#define motorMaxValYaks				10
#define motorMinValYaks				-9


/*
 * ------------------  VARIABLES GLOBALES  -----------------------------
 */
using boost::asio::ip::tcp;
boost::asio::io_service io_service;
tcp::socket* s;
using namespace std;

/*
 * -------------  DEFINICION DE FUNCIONES PRIVADAS  --------------------
 */

void connectSocket(void){
  tcp::resolver resolver(io_service);
  tcp::resolver::query query(SERVER_HOST, SERVER_PORT);
  tcp::resolver::iterator endpoint_iterator = resolver.resolve(query);

  s = new tcp::socket(io_service);
  boost::system::error_code error;
  s->connect(*endpoint_iterator);
}

void disconnectSocket(void){
  s->close();
}

bool request_data(const std::string& cmd, std::vector<string>& output) {
  vector<char> data(512);

  s->send(boost::asio::buffer(cmd + "\n"));
  s->receive(boost::asio::buffer(data));

  std::string data_str(&data[0], data.size());
  boost::split(output, data_str, boost::is_any_of(","));
  std::string lowercase_cmd(cmd);
  boost::algorithm::to_lower(lowercase_cmd);
  if (output[0] != lowercase_cmd) return false;
  else return true;
}

void readProximitySensors(int *sensors){
  std::vector<std::string> strs;
  if (!request_data("N", strs)) {
    for(int i = 0; i < 8; i++) sensors[i] = 0;
    return;
  }
  else {
    for (unsigned int i = 0; i < 8 && i < strs.size(); i++)
      sensors[i] = atoi(strs[i].c_str());
  }
}

void readLightSensors(int *sensors){
  std::vector<std::string> strs;
  if (!request_data("O", strs)) {
    for(int i = 0; i < 8; i++) sensors[i] = 0;
    return;
  }
  else {
    for (unsigned int i = 0; i < 8 && i < strs.size(); i++)
      sensors[i] = atoi(strs[i].c_str());
  }
}

float readGroundSensor(){
  std::vector<std::string> strs;
  if (!request_data("#G", strs)) return 0;
  else return atof(strs[0].c_str());
}

float readEnergySensor(){
  std::vector<std::string> strs;
  if (!request_data("#E", strs)) return 0;
  else return atof(strs[0].c_str());
}

void setMotors(int *motors){
  ostringstream ostr;
  ostr << "D," << motors[0] << "," << motors[1] << endl;
  s->send(boost::asio::buffer(ostr.str()));

  vector<char> data(512);
  s->receive(boost::asio::buffer(data)); // discard response
}

void normalizarSensores( int *sensors, int tamanio, std::string tipo ){
	/*
	// muestro lo obtenido...
	printf("Valor sensores Desnorm es: [");
	for(int i=0; i<tamanio; i++)
		printf("%d , ", sensors[i] );
	printf("] \n");
	*/

	float valorNorm = 0;
	if( tipo == "proximity" )
		for(int i=0; i<tamanio; i++){
			// normalizo el valor
			valorNorm = ((((float)(((float)(sensors[i]*100))/sensorProxMax))*sensorMaxNormalizado)/100) ;
			// saturo por si las dudas
			if( valorNorm < sensorMinNormalizado ) valorNorm = sensorMinNormalizado;
			if( valorNorm > sensorMaxNormalizado ) valorNorm = sensorMaxNormalizado;
			sensors[i] = (int)valorNorm;
		}
	if( tipo == "light" )
		for(int i=0; i<tamanio; i++){
			// normalizo el valor
			valorNorm = ((((float)(((float)(sensors[i]*100))/sensorLightMax))*sensorMaxNormalizado)/100) ;
			// saturo por si las dudas
			if( valorNorm < sensorMinNormalizado ) valorNorm = sensorMinNormalizado;
			if( valorNorm > sensorMaxNormalizado ) valorNorm = sensorMaxNormalizado;
			sensors[i] = (int)valorNorm;
		}

	/*
	// muestro lo obtenido...
	printf("Valor sensores Norm es: [");
	for(int i=0; i<tamanio; i++)
		printf("%d , ", sensors[i] );
	printf("] \n \n");
	*/
}

void desNormalizarMotores( int *motors, int tamanio ){
	/*
	// muestro lo obtenido...
	printf("Valor motores Norm es: [");
	for(int i=0; i<tamanio; i++)
		printf("%d , ", motors[i] );
	printf("] \n");
	*/

	float valorDesnorm = 0;
	for(int i=0; i<tamanio; i++){
		// normalizo el valor
		valorDesnorm = ((((float)(((float)(motors[i]*100))/motorMax))*motorMaxDesnormalizado)/100) ;
		// saturo por si las dudas
		//if( valorDesnorm < motorMinDesnormalizado ) valorDesnorm = motorMinDesnormalizado;
		//if( valorDesnorm > motorMaxDesnormalizado ) valorDesnorm = motorMaxDesnormalizado;
		if( valorDesnorm < motorMinValYaks ) valorDesnorm = motorMinValYaks;
		if( valorDesnorm > motorMaxValYaks ) valorDesnorm = motorMaxValYaks;
		motors[i] = (int)valorDesnorm;
	}

	/*
	// muestro lo obtenido...
	printf("Valor motores Desnorm es: [");
	for(int i=0; i<tamanio; i++)
		printf("%d , ", motors[i] );
	printf("] \n \n");
	*/
}

// ---------------------------------------------------------------------
// --------------  FIN DE LAS FUNCIONES PRIVADAS  ----------------------
// ---------------------------------------------------------------------

void inicializarRAL(){
	connectSocket();
}

void finalizarRAL(){
	disconnectSocket();
}

std::vector<std::string> getListaSensores(){
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
	sensors[11] = SENSOR_11;
	sensors[12] = SENSOR_12;
	sensors[13] = SENSOR_13;
	sensors[14] = SENSOR_14;
	sensors[15] = SENSOR_15;
	return sensors;
}

std::vector<std::string> getListaActuadores(){
	std::vector<std::string> motors(2);
	motors[0] = MOTOR_00;
	motors[1] = MOTOR_01;
	return motors;
}

std::vector<Item> getEstadoSensores(){
	Item item;
	std::vector<std::string> sensorsName = getListaSensores();
	//std::vector<Item> sensors(16);
	std::vector<Item> sensors;
	int proximitySensors[8];
	int lightSensors[8];

	readProximitySensors(proximitySensors);
	readLightSensors(lightSensors);

	// normalizo los valores de sensores para la GUI...
	normalizarSensores( proximitySensors, 8, "proximity" );
	normalizarSensores( lightSensors, 8, "light" );

	for(int i=0; i<8; i++){
		item.id = sensorsName[i];
		item.valor = proximitySensors[i];
		sensors.push_back(item);
	}
	for(int i=8; i<16; i++){
		item.id = sensorsName[i];
		item.valor = lightSensors[i-8];
		sensors.push_back(item);
	}
	return sensors;
}

unsigned long getFrecuenciaTrabajo(){
	return CTR_FREC;
}

void setEstadoActuadores(std::vector<Item> actuators){
	int motors[2];
	//int* motors[2];
	for(unsigned int i=0; i < actuators.size(); i++){
		//printf("Valor motor %s es %d \n", (actuators[i].id).c_str(), actuators[i].valor);
		if ( actuators[i].id == MOTOR_00){ motors[0] = actuators[i].valor;}
		if ( actuators[i].id == MOTOR_01){ motors[1] = actuators[i].valor;}
	}

	// desnormalizo los valores de motores de la GUI...
	desNormalizarMotores( motors, 2 );

	// seteo
	setMotors(motors);
}
