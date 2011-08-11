#include <RAL.h>
#include "yaksRAL.h"

// DEFINICION DE VARIABLES DE NORMALIZACION DE SENSORES Y MOTORES
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


// ---------------------------------------------------------------------
// ------------------  VARIABLES GLOBALES  -----------------------------
// ---------------------------------------------------------------------
int sock; // global variable sock


// ---------------------------------------------------------------------
// -------------  DEFINICION DE FUNCIONES PRIVADAS  --------------------
// ---------------------------------------------------------------------

int connectSocket(void){
	struct sockaddr_in stSockAddr;
	int Res;
	int SocketFD = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);

	if ( -1 == SocketFD ){
		perror("cannot create socket");
		exit(EXIT_FAILURE);
	}

	memset(&stSockAddr, 0, sizeof(stSockAddr));

	stSockAddr.sin_family = AF_INET;
	stSockAddr.sin_port = htons(SERVER_PORT);
	Res = inet_pton(AF_INET, SERVER_HOST, &stSockAddr.sin_addr);

	if (0 > Res){
		perror("error: first parameter is not a valid address family");
		close(SocketFD);
		exit(EXIT_FAILURE);
	}
	else if (0 == Res){
		perror("char string (second parameter does not contain valid ipaddress");
		close(SocketFD);
		exit(EXIT_FAILURE);
	}

	if (-1 == connect(SocketFD, (const sockaddr *)&stSockAddr, sizeof(stSockAddr))){
		perror("connect failed");
		close(SocketFD);
		exit(EXIT_FAILURE);
	}

	return SocketFD;	
}

int disconnectSocket(int SocketFD){
	shutdown(SocketFD, SHUT_RDWR);
	close(SocketFD);
	return 0;
}

void readProximitySensors(int *sensors){
	char buf[512], *p;
	int i;

	send(sock,"N\n",strlen("N\n"),0);
	recv(sock,buf,sizeof(buf),0);

	p=strchr(buf,'\n');
	if( p!=NULL )
		*p=0;
	//printf("%s\n",buf);

	i=0;
	p=strtok(buf,",");

	if( strcmp(p,"n") ){
		/* error. Set all to 0 */
		for( i=0; i<8; i++ )
			sensors[i]=0;
		return;
	}

	p=strtok(NULL,",");
	while( i<8 && p!=NULL ){
		sensors[i++] = (atoi(p));
		p=strtok(NULL,",");
	}
}

void readLightSensors(int *sensors){
	char buf[512], *p;
	int i;

	send(sock,"O\n",strlen("O\n"),0);
	recv(sock,buf,sizeof(buf),0);

	p=strchr(buf,'\n');
	if( p!=NULL )
		*p=0;
	//printf("%s\n",buf);

	i=0;
	p=strtok(buf,",");

	if( strcmp(p,"o") ){
		/* error. Set all to 0 */
		for( i=0; i<8; i++ )
			sensors[i]=0;
		return;
	}

	p=strtok(NULL,",");
	while( i<8 && p!=NULL ){
		sensors[i++] = (atoi(p));
		p=strtok(NULL,",");
	}
}

float readGroundSensor(){
	char buf[512], *p;

	send(sock,"#G\n",strlen("#G\n"),0);
	recv(sock,buf,sizeof(buf),0);

	p=strchr(buf,'\n');
	if( p!=NULL )
		*p=0;
	printf("%s\n",buf);

	/* if the string does not begins with "#g,", something bad happened */
	if( strncmp(buf,"#g,",3) )
		return 0;

	return (float)atof(&buf[3]);
}

float readEnergySensor(){
	char buf[512], *p;

	send(sock,"#E\n",strlen("#E\n"),0);
	recv(sock,buf,sizeof(buf),0);

	p=strchr(buf,'\n');
	if( p!=NULL )
		*p=0;
	printf("%s\n",buf);

	/* if the string does not begins with "#e,", something bad happened */
	if( strncmp(buf,"#e,",3) )
		return 0;

	return (float)atof(&buf[3]);
}

void setMotors(int *motors){
	char cmd[512], resp[512];

	sprintf(cmd,"D,%d,%d\n",motors[0],motors[1]);
	send(sock,cmd,strlen(cmd),0);

	/* discard the answer */
	recv(sock,resp,sizeof(resp),0);
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
	sock = connectSocket();
}

void finalizarRAL(){
	int res = disconnectSocket(sock);
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
	for(int i=0; i < actuators.size(); i++){
		//printf("Valor motor %s es %d \n", (actuators[i].id).c_str(), actuators[i].valor);
		if ( actuators[i].id == MOTOR_00){ motors[0] = actuators[i].valor;}
		if ( actuators[i].id == MOTOR_01){ motors[1] = actuators[i].valor;}
	}
	
	// desnormalizo los valores de motores de la GUI...
	desNormalizarMotores( motors, 2 );
	
	// seteo
	setMotors(motors);	
}
