#include <RAL.h>
#include "kheperaRAL.h"

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
int com1_fd;					// File Descriptor del port COM1
struct termios com1_old_set; 	// Para guardar la configuración que tenía el COM1 // necesita "termios.h"
struct termios com1_new_set; 	// Para setear la configuración al COM1 // necesita "termios.h"


// ---------------------------------------------------------------------
// -------------  DEFINICION DE FUNCIONES PRIVADAS  --------------------
// ---------------------------------------------------------------------

int connectCOM1(){
	// abro el COM1 en modo "non-blocking"
		// O_RDWR - open read-write.
		// O_NOCTTY - open TTY without it becoming controlling tty.
		// O_NONBLOCK ó O_NDELAY - open in non-blocking mode (read will return immediately)
	com1_fd = open( COM1, O_RDWR | O_NOCTTY | O_NONBLOCK );

	// chequeo si abrio bien...
	if (com1_fd < 0){
		perror(COM1);
		//std::cout << "Error: al abrir el puerto COM1 !!!" << std::endl;
		return 1;
	}

	// salvo la configuración actual del puerto
	tcgetattr( com1_fd, &com1_old_set );

	// seteo la nueva configuración para el puerto
	com1_new_set.c_cflag = ( BAUDRATE | CRTSCTS | DATABITS_8 | STOPBITS_2 | PARITYON | PARITY | CLOCAL | CREAD );
	com1_new_set.c_iflag = IGNPAR;
	com1_new_set.c_oflag = 0;
	com1_new_set.c_lflag = 0;
	com1_new_set.c_cc[VMIN] = 1;
	com1_new_set.c_cc[VTIME] = 0;
	tcflush( com1_fd, TCIFLUSH );
	tcsetattr( com1_fd, TCSANOW, &com1_new_set ); // la escribo

	return 0;
}

int disconnectCOM1(){
	tcsetattr( com1_fd, TCSANOW, &com1_old_set ); // restauro la configuración que tenía el puerto
	close( com1_fd ); // cierro el COM1
	return 0;
}

void readProximitySensors(int *sensors){
	char buf[100], *p;
	int i;
	bool flag;

	// escribo en el puerto el comando específico
	write( com1_fd, "N\n" , strlen("N\n") );

	// leo del puerto la respuesta
	flag = true;
	i = 0;
	while( flag ){
		read( com1_fd, &buf[i], 1 );
		i++;
		if( buf[i-1] == '\n' )
			flag = false;
	}

	p = strchr( buf, '\n' );
	if( p != NULL )
		*p = 0;
	//printf( "%s\n", buf );

	i = 0;
	p = strtok( buf, "," );

	if( strcmp( p, "n" ) ){
		// error: Set all to 0 
		for( i = 0; i < 8; i++ )
			sensors[i] = 0;
		return;
	}

	p = strtok( NULL, "," );
	while( i < 8 && p != NULL ){
		sensors[i++] = ( atoi(p) );
		p = strtok( NULL, "," );
	}

	// MUESTRO EL BUFFER LEIDO
	//std::cout << "BUFFER LEIDO PROXIMITY: [";
	//for( i = 0; i < 8; i++ )
		//std::cout << sensors[i] << ", ";
	//std::cout << "]" << std::endl;
}

void readLightSensors(int *sensors){
	char buf[100], *p;
	int i;
	bool flag;

	// escribo en el puerto el comando específico
	write( com1_fd, "O\n" , strlen("O\n") );

	// leo del puerto la respuesta
	flag = true;
	i = 0;
	while( flag ){
		read( com1_fd, &buf[i], 1 );
		i++;
		if( buf[i-1] == '\n' )
			flag = false;
	}

	p = strchr( buf, '\n' );
	if( p != NULL )
		*p = 0;
	//printf( "%s\n", buf );

	i = 0;
	p = strtok( buf, "," );

	if( strcmp( p, "o" ) ){
		// error: Set all to 0 
		for( i = 0; i < 8; i++ )
			sensors[i] = 0;
		return;
	}

	p = strtok( NULL, "," );
	while( i < 8 && p != NULL ){
		sensors[i++] = ( atoi(p) );
		p = strtok( NULL, "," );
	}


		// MUESTRO EL BUFFER LEIDO
		//std::cout << "BUFFER LEIDO LIGHT: [";
		//for( i = 0; i < 8; i++ )
			//std::cout << sensors[i] << ", ";
		//std::cout << "]" << std::endl;

}

float readGroundSensor(){
	char buf[100], *p;
	int i;
	bool flag;
		
	// escribo en el puerto el comando específico
	write( com1_fd, "#G\n" , strlen("#G\n") );

	// leo del puerto la respuesta
	flag = true;
	i = 0;
	while( flag ){
		read( com1_fd, &buf[i], 1 );
		i++;
		if( buf[i-1] == '\n' )
			flag = false;
	}


	p = strchr( buf, '\n' );
	if( p != NULL )
		*p = 0;
	//printf("%s\n",buf);

	// si el string no comienza con "#g," algo no andubo bien...
	if( strncmp( buf, "#g,", 3 ) )
		return 0;

	return (float)atof( &buf[3] );
}

float readEnergySensor(){
	char buf[100], *p;
	int i;
	bool flag;
		
	// escribo en el puerto el comando específico
	write( com1_fd, "#E\n" , strlen("#E\n") );

	// leo del puerto la respuesta
	flag = true;
	i = 0;
	while( flag ){
		read( com1_fd, &buf[i], 1 );
		i++;
		if( buf[i-1] == '\n' )
			flag = false;
	}


	p = strchr( buf, '\n' );
	if( p != NULL )
		*p = 0;
	//printf("%s\n",buf);

	// si el string no comienza con "#e," algo no andubo bien...
	if( strncmp( buf, "#e,", 3 ) )
		return 0;

	return (float)atof( &buf[3] );
}

void setMotors(int *motors){
	char cmd[100];
	char resp[100];
	int i;
	bool flag;

	// convierto los valores recibidos a chars...
	sprintf( cmd, "D,%d,%d\n", motors[0], motors[1] );
	
	// escribo en el puerto el comando específico
	write( com1_fd, cmd , strlen(cmd) );

	// leo del puerto la respuesta y la descarto!!!
	flag = true;
	i = 0;
	while( flag ){
		read( com1_fd, &resp[i], 1 );
		i++;
		if( resp[i-1] == '\n' )
			flag = false;
	}
}

void normalizarSensores( int *sensors, int tamanio, std::string tipo ){
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
}

void desNormalizarMotores( int *motors, int tamanio ){
	float valorDesnorm = 0;
	for(int i=0; i<tamanio; i++){
		// normalizo el valor
		valorDesnorm = ((((float)(((float)(motors[i]*100))/motorMax))*motorMaxDesnormalizado)/100) ;
		// saturo por si las dudas
		if( valorDesnorm < motorMinDesnormalizado ) valorDesnorm = motorMinDesnormalizado;
		if( valorDesnorm > motorMaxDesnormalizado ) valorDesnorm = motorMaxDesnormalizado;
		//if( valorDesnorm < motorMinValYaks ) valorDesnorm = motorMinValYaks;
		//if( valorDesnorm > motorMaxValYaks ) valorDesnorm = motorMaxValYaks;
		motors[i] = (int)valorDesnorm;
	}
}

// ---------------------------------------------------------------------
// --------------  FIN DE LAS FUNCIONES PRIVADAS  ----------------------
// ---------------------------------------------------------------------


void inicializarRAL(){
	int res = connectCOM1();
	if (res == 1){}
		//std::cout << "Error: no se pudo inicializar el RAL !!!" << std::endl;
}

void finalizarRAL(){
	int res = disconnectCOM1();
	if (res == 1){}
		//std::cout << "Error: no se pudo finalizar el RAL !!!" << std::endl;
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
