#include <libplayerc++/playerc++.h>
#include <libplayerc++/playerclient.h>
#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <algorithm>
#include <RAL.h>
using namespace std;

/* constantes */
#define SENSOR_00			"telemetro.270"
#define SENSOR_01			"telemetro.315"
#define SENSOR_02			"telemetro.0"
#define SENSOR_03			"telemetro.45"
#define SENSOR_04			"telemetro.90"
#define SENSOR_05			"telemetro.180"
//#define SENSOR_06                       "sonar.0"
//#define SENSOR_07                       "linea.0"
//#define SENSOR_08                       "linea.1"
//#define SENSOR_09                       "contacto.0"
//#define SENSOR_10                       "contacto.1"

#define MAX_TELEMETER_RANGE 0.8
#define MIN_TELEMETER_RANGE 0.06

/* variables */
PlayerCc::PlayerClient* player_client;
PlayerCc::Position2dProxy* position_proxy;
PlayerCc::RangerProxy* ranger_proxy;

/* funciones */
extern "C" {
void inicializarRAL(void) {
  player_client = new PlayerCc::PlayerClient("localhost");
  position_proxy = new PlayerCc::Position2dProxy(player_client);
  ranger_proxy = new PlayerCc::RangerProxy(player_client);
  
  player_client->StartThread();
  player_client->Read();
  position_proxy->SetMotorEnable(true);
}

void finalizarRAL(void) {
  position_proxy->SetMotorEnable(false);
  player_client->StopThread();
  delete ranger_proxy;
  delete position_proxy;
  delete player_client;
}

std::vector<std::string> getListaSensores(){
  //std::vector<std::string> sensors(10);
  std::vector<std::string> sensors(6);
  sensors[0] = SENSOR_00;
  sensors[1] = SENSOR_01;
  sensors[2] = SENSOR_02;
  sensors[3] = SENSOR_03;
  sensors[4] = SENSOR_04;
  sensors[5] = SENSOR_05;
  //sensors[6] = SENSOR_06;
  //sensors[7] = SENSOR_07;
  //sensors[8] = SENSOR_08;
  //sensors[9] = SENSOR_09;
  //sensors[10] = SENSOR_10;
  return sensors;
}

std::vector<std::string> getListaActuadores(void) {
  std::vector<std::string> motors(2);
  motors[0] = MOTOR_00;
  motors[1] = MOTOR_01;
  return motors;
}

std::vector<Item> getEstadoSensores(void) {  
  Item item;
  std::vector<std::string> sensorsName = getListaSensores();
  //std::vector<Item> sensors(10);
  std::vector<Item> sensors(6);
  
  // telemetros
  boost::mutex::scoped_lock(player_client->mMutex);
  
  cout << "sensores = [ ";
  for (size_t i = 0; i < 6; i++) {
    sensors[i].id = sensorsName[i];
    //double a = ranger_proxy->GetRange(i) - MIN_TELEMETER_RANGE;
    //sensors[i].valor = (int)round((max(0.0, ranger_proxy->GetRange(i) - MIN_TELEMETER_RANGE) / (MAX_TELEMETER_RANGE - MIN_TELEMETER_RANGE)) * 255);
    sensors[i].valor = (int)round((max(0.0, ranger_proxy->GetRange(i) - MIN_TELEMETER_RANGE) / (MAX_TELEMETER_RANGE - MIN_TELEMETER_RANGE)) * 100)-10;
    //sensors[i].valor = 255 - min(sensors[i].valor, 255);
    sensors[i].valor = 100 - min(sensors[i].valor, 100);
    cout << ranger_proxy->GetRange(i) - MIN_TELEMETER_RANGE << "-" << sensors[i].valor << " : " ;
  }
  cout << " ]" << endl;

  //// sonar
  //sensors[6].id = sensorsName[6];
  //sensors[6].valor = 0; // TODO: terminar
  
  //// bumpers
  //for (size_t i = 6; i < 8; i++) {
    //sensors[i].id = sensorsName[i];
    //sensors[i].valor = 0; // TODO: terminar
  //}
  
  //// LF
  //for (size_t i = 8; i < 10; i++) {
    //sensors[i].id = sensorsName[i];
    //sensors[i].valor = 0; // TODO: terminar
  //}
  
  return sensors;
}

unsigned long getFrecuenciaTrabajo(){
 // return 10000; // TODO
 return 10000; // TODO
}

void setEstadoActuadores(std::vector<Item> actuators){
  double ML = ((double)actuators[0].valor / 200.0) * 0.5;
  double MR = ((double)actuators[1].valor / 200.0) * 0.5;

  cout << "ERBPI [L:R] = [" << actuators[0].valor << ":" << actuators[1].valor << "]" << endl;  
  cout << "P/S   [L:R] = [" << ML << ":" << MR << "]" << endl;  

  const double wheelbase = 0.183;
  
  double xspeed = (ML + MR) * 0.5;
  double aspeed = (ML - MR) / wheelbase;

  //cout << "act0/1: " << actuators[0].valor << "/" << actuators[1].valor << " ML/MR: " << ML << "/" << MR << " xspeed/aspeed: " << xspeed << "/" << aspeed << endl;
  position_proxy->SetSpeed(xspeed, aspeed);
}
}
