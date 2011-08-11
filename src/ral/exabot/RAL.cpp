#include <RAL.h>
#include "exabotRAL.h"

// las posiciones y asignaciones de los telémetros
// pueden verse correctamente en "exabot_telemetros_posiciones_02.png"
// que se encuentra adjunto a este RAL.h

extern unsigned int linealizar(unsigned int pos);

// DEFINICION DE VARIABLES DE NORMALIZACION DE SENSORES Y MOTORES
#define motorMax					100
#define motorMin					-100
#define motorMaxDesnormalizado		30
#define motorMinDesnormalizado		-30
#define bumperMax					255 	// el bumper está apretado
#define bumperMin					0 		// el bumper está libre
#define bumperMaxNormalizado		100
#define bumperMinNormalizado		0
#define lineMax	 					1 		// el linefollowing está viendo la linea
#define lineMin 					255		// el linefollowing hace infinito que no ve la linea. En realidad, te devuelve "hace cuanto que NO ve la linea". Si el valor es 1 => estas viendo la linea en ese momento. Si el valor es entre 2:255 => no estas viendo la linea.
#define lineMaxNormalizado			100
#define lineMinNormalizado			0
#define sonarMax					31250  	// 31250 = 0% = 25 ms = 4 m // por como está configurado el hardware del robot, el sonar está entregando un valor de 2 bytes que contiene "el tiempo transcurrido" que le costó ver el objeto. Este rango de tiempo tiene su correlación con distancia. Si el valor de los 2 bytes es "125 = 100% = 100 us = 1 cm", valor mínimo, quiere decir que "está viendo mucho", tiene el objeto muy cerca. Si el valor de los 2 bytes es "31250 = 0% = 25 ms = 4 m", valor máximo, quiere decir que "está viendo poco", tiene el objeto muy lejos. Tener en cuenta que los valores están al revés en cuanto al porcentaje, es decir, que hay que invertir, como con el telemetro... Hay que recortar (saturar). Los valores < 125 deberían ser 0%. Los valores > 31250 deberían ser 0%.
#define sonarMin					125		// 125 = 100% = 100 us = 1 cm // por como está configurado el hardware del robot, el sonar está entregando un valor de 2 bytes que contiene "el tiempo transcurrido" que le costó ver el objeto. Este rango de tiempo tiene su correlación con distancia. Si el valor de los 2 bytes es "125 = 100% = 100 us = 1 cm", valor mínimo, quiere decir que "está viendo mucho", tiene el objeto muy cerca. Si el valor de los 2 bytes es "31250 = 0% = 25 ms = 4 m", valor máximo, quiere decir que "está viendo poco", tiene el objeto muy lejos. Tener en cuenta que los valores están al revés en cuanto al porcentaje, es decir, que hay que invertir, como con el telemetro... Hay que recortar (saturar). Los valores < 125 deberían ser 0%. Los valores > 31250 deberían ser 0%.
#define sonarMaxNormalizado			100
#define sonarMinNormalizado			0
// estos son porque el ExaBot no gira bien los motores si le mandas menos de 15%...
#define motorMinValExa				20
#define motorMinValExaNeg			-20
#define motorMinValExaCero			10
#define motorMinValExaNegCero		-10
float motorDesnormalizadoFactor = (float) motorMaxDesnormalizado/motorMax;

// las de telemetros están en "teles_liceal.c"

// ---------------------------------------------------------------------
// ------------------  VARIABLES Y FUNCIONES DE UDP --------------------
// -------------------  ANTES ERA UN "PROCESS.H"  ----------------------
// ---------------------------------------------------------------------

// el tamaño de los buffers es siempre uno mas que el del paquete udp porque
// tiene un semaforo para saber cuando leer y escribir.
#define SND_SIZE 4
#define UDP_SND_BUF_SIZE 5
#define RCV_SIZE 18
#define UDP_RCV_BUF_SIZE 19
void udp_receive(void);
void udp_send(void);


// ---------------------------------------------------------------------
// ------------------  VARIABLES GLOBALES  -----------------------------
// ---------------------------------------------------------------------
int shmid_udp_rcv;
int shmid_udp_send;
char cmd[SND_SIZE];
char *shared_udp_rcv;
char *shared_udp_send;
pid_t  pid1;
pid_t  pid2;


// ---------------------------------------------------------------------
// -------------  DEFINICION DE FUNCIONES PRIVADAS  --------------------
// ---------------------------------------------------------------------

void enviar_comando( void ){
	unsigned int i;
	
	//printf("\n ENTRE A ENVIAR COMANDO!\n");
	
	for(i=0;i< SND_SIZE;i++)
		shared_udp_send[i+1] = cmd[i];
	//envia!
	shared_udp_send[0] = 0xFF;

	//for(i=0; i< SND_SIZE+1;i++)
		//printf("enviar_comando: %x  ::  ", (char)shared_udp_send[i]);
}

void cleartoendofline( void ){
	char ch;
	ch = getchar();
	while( ch != '\n' )
		ch = getchar();
}

void terminar_udp_send(int sig){
	//printf("LLAMÓ A 'terminar_udp_send'\n");
	exit(sig); 
}

void terminar_udp_receive(int sig){
	//printf("LLAMÓ A 'terminar_udp_receive'\n");
	exit(sig); 
}

void udp_send( void ){
	signal( SIGINT, terminar_udp_send ); // configurar la rutina de atención de SIGINT
	signal( SIGTERM, terminar_udp_send ); // configurar la rutina de atención de SIGINT
	
	char *shared_udp_send;
	int sock;
	struct sockaddr_in sa;
	char send_buffer[SND_SIZE];
	unsigned int i;
	shared_udp_send = ((char *)shmat( shmid_udp_send, NULL, 0 ));

	sock = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);
	if( -1 == sock ){ // if socket failed to initialize, exit 
		printf("Error Creating Socket");
		exit(EXIT_FAILURE);
	}

	memset(&sa, 0, sizeof(sa));
	sa.sin_family = AF_INET;
	sa.sin_addr.s_addr = htonl(IP_EXA);
	sa.sin_port = htons(7654);

	while(1){
		//en el primer byte tengo el flag que me dice si
		//tengo algo nuevo para mandar
		
		if(shared_udp_send[0]){
			for(i=0; i< SND_SIZE; i++) send_buffer[i] = shared_udp_send[i+1];

			sendto(sock, send_buffer , SND_SIZE, 0,(struct sockaddr *)&sa, sizeof (struct sockaddr_in));
			shared_udp_send[0] = 0x00;

			//printf("envie a pc104: [ ");
			//for(i = 0; i < SND_SIZE ; i++ )	printf(" %x ,", (unsigned char)send_buffer[i]);
			//printf("] \n");

		}
	}
}

void udp_receive( void ){
	signal( SIGINT, terminar_udp_receive ); // configurar la rutina de atención de SIGINT
	signal( SIGTERM, terminar_udp_receive ); // configurar la rutina de atención de SIGINT

	unsigned int i;
	int sock = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);
	struct sockaddr_in sa;
	socklen_t fromlen;
        size_t recsize;
	char *shared_udp_rcv;
	char *shared_exit;
	//los comandos son de RCV_SIZE bytes: esto puede cambiarse en threads.h
	char rcv_buffer[RCV_SIZE];
	shared_udp_rcv = ((char *)shmat( shmid_udp_rcv, NULL, 0 ));

	memset(&sa, 0, sizeof(sa));
	sa.sin_family = AF_INET;
	sa.sin_addr.s_addr = INADDR_ANY;
	sa.sin_port = htons(7655);

	if( -1 == bind(sock,(struct sockaddr *)&sa, sizeof(struct sockaddr)) ){
		perror("error bind failed");
		close(sock);
		exit(EXIT_FAILURE);
	}

	while( 1 ){
		//printf ("recv test....\n");
		recsize = recvfrom(sock, (void *)rcv_buffer, RCV_SIZE, 0, (struct sockaddr *)&sa, &fromlen);

		if( recsize < 0 )
			fprintf(stderr, "%s\n", strerror(errno));
		else{
			//printf("recsize: %d: %d %d\n ",recsize,buffer[0],buffer[1]);
			//sleep(1);
			//printf("datagram: %s\n",buffer);

			for(i = 1; i < RCV_SIZE+1; i++)
				shared_udp_rcv[i] = rcv_buffer[i-1];

			
			printf("recsize: %d \n", (int) recsize);
			printf("[ ");
			for(i = 0; i < RCV_SIZE + 1; i++ )
				printf(" %u ,", (unsigned char)shared_udp_rcv[i]);
			printf("] \n");
			//printf("telemetro2: %u \n", (unsigned char)shared_udp_rcv[8]);
			
			
			shared_udp_rcv[0] = 0xFF; //semaforo que indica que recibi algo
		}
	}
}

int normalizarSonar( int valorSonar ){
	// OJO: por como está configurado el hardware del robot, el sonar está entregando un valor de 2 bytes
	// 		que contiene "el tiempo transcurrido" que le costó ver el objeto.
	// 		Este rango de tiempo tiene su correlación con distancia.
	// 		Si el valor de los 2 bytes es "125 = 100% = 100 us = 1 cm", valor mínimo,
	// 		quiere decir que "está viendo mucho", tiene el objeto muy cerca.
	// 		Si el valor de los 2 bytes es "31250 = 0% = 25 ms = 4 m", valor máximo,
	// 		quiere decir que "está viendo poco", tiene el objeto muy lejos.

	// 		Tener en cuenta que los valores están al revés en cuanto al porcentaje,
	// 		es decir, que hay que invertir, como con el telemetro...
	// 		Además, hay que recortar (saturar).
	// 		Los valores < 125 deberían ser 0%.
	// 		Los valores > 31250 deberían ser 0%.

	unsigned int valorNorm = 0;
	
	if( valorSonar > sonarMax ) 		valorNorm = sonarMinNormalizado;
	else if( valorSonar < sonarMin ) 	valorNorm = sonarMinNormalizado;
		 else 							valorNorm = (unsigned int)(( (float)(100-((float)(((float)((valorSonar-sonarMin)*100))/sonarMax)))*sonarMaxNormalizado)/100) ;

	return valorNorm;
}

int normalizarLinea( int valorLinea ){
	// Funcionamiento: por defecto devuelve 0% (hace infinito que no ve la linea). Si el valor es lineMax (1), entonces devuelve 100% (está viendo la linea)...
	if( valorLinea == lineMax )
		return lineMaxNormalizado; 		// el linefollowing está viendo la linea
	else
		return lineMinNormalizado;		// el linefollowing hace infinito que no ve la linea. En realidad, el sensor te devuelve "hace cuanto que NO ve la linea". Si el valor es 1 => estas viendo la linea en ese momento. Si el valor es entre 2:255 => no estas viendo la linea. Pero nosotros devolvemos "0%"...
}

int normalizarBumper( int valorBumper ){
	// Funcionamiento: por defecto devuelve 0% (bumper libre). Si el valor es bumperMax (255), entonces devuelve 100% (bumper apretado)...
	if( valorBumper == bumperMax )
		return bumperMaxNormalizado; 	// el bumper está apretado
	else
		return bumperMinNormalizado; 	// el bumper está libre
}

// ---------------------------------------------------------------------
// --------------  FIN DE LAS FUNCIONES PRIVADAS  ----------------------
// ---------------------------------------------------------------------

extern "C" {
void inicializarRAL(void) {
	unsigned int i;
	int sock, sock_send;
	
	// 1) tengo dos procesos: udp_receive y udp_send
	// 2) tengo dos porciones de memoria shareada: una para comunicar la main_app con el udp_receive y otra para comunicar el main_app con el udp_send.
	// 2.1) -> inicializar shared memory para UDP receive, la variable UDP_RCV_BUF_SIZE puede cambiarse...
	shmid_udp_rcv = shmget(IPC_PRIVATE, UDP_RCV_BUF_SIZE, 0660 | IPC_CREAT);
	shared_udp_rcv = ((char *)shmat(shmid_udp_rcv, NULL, 0 ));

	for( i = 0; i< UDP_RCV_BUF_SIZE; i++ )
		shared_udp_rcv[i] = 0;

	// 2.2) -> inicializar shared memory para UDP send, la variable UDP_SND_BUF_SIZE puede cambiarse...
	shmid_udp_send = shmget(IPC_PRIVATE, UDP_SND_BUF_SIZE, 0660 | IPC_CREAT);
	shared_udp_send = ((char *)shmat(shmid_udp_send, NULL, 0 ));

	for( i = 0; i< UDP_SND_BUF_SIZE; i++ )
		shared_udp_send[i] = 0;

	// 3) forkeamos!
	pid1 = fork();
	if( pid1 == 0 ){ // hijo1
		// con "setsid()" desatacheo este hijo del padre (Core)...
		// Es para que el hijo no dependan de la misma consola...
		// Es decir, no muera con el "Ctrl+C", "kill -int Core" o "kill Core" del padre (Core)...
		setsid(); // desatachea el hijo del padre...
		udp_receive();
	}
	else{
		pid2 = fork();
		if( pid2 == 0 ){ // hijo2
			// con "setsid()" desatacheo este hijo del padre (Core)...
			// Es para que el hijo no dependan de la misma consola...
			// Es decir, no muera con el "Ctrl+C", "kill -int Core" o "kill Core" del padre (Core)...
			setsid(); // desatachea el hijo del padre...
			udp_send();
		}
		else{ // padre!! -> en realidad no es el padre, porque termina, no tiene un while(1), el padre es el CORE...
			// 1) inicializo buffer de comando
			for( i = 0; i< SND_SIZE; i++ )
				cmd[i] = 0xFF;
			// 2) memoria shareada con el udp_rcv y el udp_send
			shared_udp_rcv = ((char *)shmat( shmid_udp_rcv, NULL, 0 ));
			shared_udp_send = ((char *)shmat( shmid_udp_send, NULL, 0 ));
			// 3) mando los comandos para prender todo!
			cmd[0] = 0x02;			// comando para los sensores
			cmd[1] = 0X01; 			// señal prender sensores
			cmd[2] = 0x00;			// los primeros 3 telemetros...
			enviar_comando();
			usleep(USLEEP_TIME);
			cmd[2] = 0x01; 			// prender los segundos 3 telemetros...
			enviar_comando();
			usleep(USLEEP_TIME);
			cmd[2] = 0x02;			// prender los terceros 3 telemetros...
			enviar_comando();
			usleep(USLEEP_TIME);
			cmd[2] = 0x03; 			// prender el sonar...
			enviar_comando();
			usleep(USLEEP_TIME);
			cmd[2] = 0x04; 			// prender los linefollowing...
			enviar_comando();
			usleep(USLEEP_TIME);
			cmd[2] = 0x05; 			// prender los bumpers...
			enviar_comando();
			usleep(USLEEP_TIME);
		}
	}
	//if (res == 1){}
		//std::cout << "Error: no se pudo inicializar el RAL !!!" << std::endl;
}

void finalizarRAL(){
	// tiempo para que procese el comando de valor 0 a los motores, que fue llamado inmediatamente antes...
	usleep(USLEEP_TIME);	
	// cuando termino killeo a mis dos hijos: udp_send y udp_receive
	kill(pid1, SIGINT);
	kill(pid2, SIGINT);
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
	std::vector<Item> sensors;
	
	// primero linealizamos los telemetros con la funcion linealizar
	// que nos da la distancia entre 60 y 800 mm y luego 
	// la normalizamos para que el Core lo interprete bien.
	// esto está todo en "teles_lineal.c"

	item.id = sensorsName[0];
	item.valor = linealizar(shared_udp_rcv[5]);		// valor de telémetro conectado en AN0
	sensors.push_back(item);

	item.id = sensorsName[1];
	item.valor = linealizar(shared_udp_rcv[6]);		// valor de telémetro conectado en AN1
	sensors.push_back(item);

	item.id = sensorsName[2];
	item.valor = linealizar(shared_udp_rcv[8]);		// valor de telémetro conectado en AN3
	sensors.push_back(item);

	item.id = sensorsName[3];
	item.valor = linealizar(shared_udp_rcv[9]);		// valor de telémetro conectado en AN4
	sensors.push_back(item);

	item.id = sensorsName[4];
	item.valor = linealizar(shared_udp_rcv[11]);	// valor de telémetro conectado en AN6
	sensors.push_back(item);

	item.id = sensorsName[5];
	item.valor = linealizar(shared_udp_rcv[12]);	// valor de telémetro conectado en AN7
	sensors.push_back(item);

	// con el sonar, bumpers y linefollowings,
	// normalizamos primero y luego salvamos

	item.id = sensorsName[6];
	item.valor = normalizarSonar( ((unsigned char)shared_udp_rcv[14])*256 + (unsigned char)shared_udp_rcv[13] );	// valor de sonar = (parte más significativa * 256) + parte menos significativa
	sensors.push_back(item);
	
	item.id = sensorsName[7];
	item.valor = normalizarLinea( (unsigned char)shared_udp_rcv[15] );		// valor de sensor de linea izquierdo
	sensors.push_back(item);

	item.id = sensorsName[8];
	item.valor = normalizarLinea( (unsigned char)shared_udp_rcv[16] );		// valor de sensor de linea derecho
	sensors.push_back(item);

	item.id = sensorsName[9];
	item.valor = normalizarBumper( (unsigned char)shared_udp_rcv[17] );		// valor de sensor de contacto izquierdo
	//item.valor = (unsigned char)shared_udp_rcv[17] ;		// valor de sensor de contacto izquierdo
	sensors.push_back(item);

	item.id = sensorsName[10];
	item.valor = normalizarBumper( (unsigned char)shared_udp_rcv[18] );		// valor de sensor de contacto derecho
	//item.valor = (unsigned char)shared_udp_rcv[18] ;		// valor de sensor de contacto derecho
	sensors.push_back(item);

	return sensors;
}

unsigned long getFrecuenciaTrabajo(){
	return CTR_FREC;
}

void setEstadoActuadores(std::vector<Item> actuators){
	int val_mot_derecho;
	int val_mot_izquierdo;
	int motor0 = actuators[0].valor;
	int motor1 = actuators[1].valor;
	// saturo por si las dudas
	if( (motor0 > 0) and ( motor0 < motorMinValExaCero) )    					motor0 = 0;
	if( (motor0 >= motorMinValExaCero) and ( motor0 < motorMinValExa) )  		motor0 = motorMinValExa;
	if( (motor0 < 0) and ( motor0 > motorMinValExaNegCero) )    				motor0 = 0;
	if( (motor0 <= motorMinValExaNegCero) and ( motor0 > motorMinValExaNeg) )  	motor0 = motorMinValExaNeg;
	if( (motor1 > 0) and ( motor1 < motorMinValExaCero) )    					motor1 = 0;
	if( (motor1 >= motorMinValExaCero) and ( motor1 < motorMinValExa) )  		motor1 = motorMinValExa;
	if( (motor1 < 0) and ( motor1 > motorMinValExaNegCero) )    				motor1 = 0;
	if( (motor1 <= motorMinValExaNegCero) and ( motor1 > motorMinValExaNeg) )  	motor1 = motorMinValExaNeg;
	
	// desnormalizo y asigno
	if( actuators[0].id == MOTOR_00 ){ //motor izquierdo
		val_mot_izquierdo = (char)((int)( (float)(motorDesnormalizadoFactor * ((float)motor0)) ) );
		val_mot_derecho   = (char)((int)( (float)(motorDesnormalizadoFactor * ((float)motor1))) );
	}
	else{
		val_mot_izquierdo = (char)((int)( (float)(motorDesnormalizadoFactor * ((float)motor1)) ) );
		val_mot_derecho   = (char)((int)( (float)(motorDesnormalizadoFactor * ((float)motor0))) );
	}
	//valor para los dos motores.
	cmd[0] = 0x03;
	cmd[1] = val_mot_derecho;							
	cmd[2] = val_mot_izquierdo*(-1); // para que el motor vaya bien, en el mismo sentido que el otro...
	
	enviar_comando();
}
}
