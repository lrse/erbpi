#ifndef _ESTRUCTURAS_H_
#define _ESTRUCTURAS_H_

	// incluir las cosas estandard
	#include <set>
	#include <map>
	#include <math.h>
	#include <string>
	#include <vector>
	#include <iostream>
	#include <stdlib.h>
	#include <sys/timeb.h>
	using namespace std;

	#include "general.h"

	// definicion de tipos de elementos
	typedef enum {
		TIPO_SENSOR=0,
		TIPO_CAJA,
		TIPO_ACTUADOR,
		TIPO_TIMER,
		TIPO_CONTADOR
	} Elemento_t;
	
	typedef enum {
		IGUAL,
		MENOR,
		MENOR_IGUAL,
		MAYOR,
		MAYOR_IGUAL
	} Comparacion_t;
	
	typedef enum {
		RESETEAR,
		INCREMENTAR,
		DECREMENTAR
	} Accion_t;
	
	struct Punto {
      int x;
      int y;
	};

	void Error(const char* msg);

	class Elemento
	{
		public:
			
			int GetValor() const;
			
			const string& Id() const;
			
			// es implementado por cada clase hija
			virtual int Ejecutar() = 0;
			
		protected:
			
			// esto la hace una clase abstracta
			Elemento(Elemento_t tipo, const string& id, int valor_inicial = 0);
			
			int _valor;
			
		private:
			
			Elemento_t _tipo;
			const string _id;
	};

	class ElementoConEntradas : public Elemento
	{
		public:
			
			/* agrega una entrada a la caja.
			 * Se usa al construir la caja a partir del xml */
			void AgregarEntrada(Elemento* entrada);
			
		protected:
			
			// esto la hace una clase abstracta
			ElementoConEntradas(Elemento_t tipo, const string& id, int valor_inicial = 0);
			
			set<Elemento*> _entradas;
			
	};

	class Sensor : public Elemento
	{
		public:
			
			Sensor(const string& id);
			
			void ActualizarValor(int valor);
			
			int Ejecutar();
	};

	class Caja : public ElementoConEntradas
	{
		public:
			
			Caja(const string& id, const Punto punto_min, const Punto punto_max);
			
			~Caja();
			
			int Ejecutar();
			
			// int Ejecutar() :
			// para cada "i" en _entradas, acumula *(_entradas[i]).getValor(),
			// realiza resultado = función(acumulador), y luego,
			// setea el "resultado" en Elemento::_valor,
			// setea el "acumulador" en Elemento::_entrada y
			// devuelve Elmemento::_valor
			
		private:
			
			// puntos que definen la funcion lineal partida
			// que aplica la caja
			Punto _punto_min;
			Punto _punto_max;
	};

	class Actuador : public ElementoConEntradas
	{
		public:
			
			Actuador(const string& id);
			
			int Ejecutar();
			
			// int Ejecutar():
			// para cada "i" en _entradas, acumula *(_entradas[i]).getValor(),
			// luego setea el acumulador en Elemento::_valor
			// y devuelve Elmemento::_valor
			
	};

	class Timer : public Elemento
	{
		public:
		
			Timer(const string& id);
		
			void Resetear();
		
			int Ejecutar();
		
		private:
		
			struct timeb _tiempo_inicio;
			bool _primera_medicion;
			
			// funciones auxiliares
			
			int TiempoTranscurrido();
	};

	class Contador : public Elemento
	{
		public:
		
			Contador(const string& id);
		
			void Incrementar();
			void Decrementar();
			void Resetear();
			
			int Ejecutar();
	};

	struct Elementos
	{
		map<const string,Caja*>* cajas;
		map<const string,Sensor*>* sensores;
		map<const string,Actuador*>* actuadores;
		map<const string,Timer*>* timers;
		map<const string,Contador*>* contadores;
	};

	// es una interface
	class Actualizacion
	{
		public:
			
			// es implementado por cada clase hija
			virtual void Ejecutar() = 0;
			
		protected:
			
			// esto la hace una clase abstracta
			Actualizacion();
	};
	
	class ActualizacionDeTimer : public Actualizacion
	{
		public:
			
			// Constructor. recibe el timer a actualizar.
			ActualizacionDeTimer(Timer* timer);
			
			void Ejecutar();
			
		private:
			
			Timer* _timer;
	};
	
	class ActualizacionDeContador : public Actualizacion
	{
		public:
			
			// Constructor.
			// Recibe el contador y la accion que se le aplica.
			ActualizacionDeContador(Contador* contador, Accion_t accion);
			
			void Ejecutar();
			
		private:
		
			Contador* _contador;
			Accion_t _accion;
	};
	
	class Condicion
	{
		public:
			
			// Constructor
			Condicion(const string& id_elemento, Comparacion_t comparacion, int umbral);
			
			// Devuelve verdadero si dado un estado de elementos
			// se cumple la condicion. Devuelve falso en caso contrario.
			bool SeCumple(const Elementos& elementos) const;
			
		private:
			
			
			const string _id_elemento;
			Comparacion_t _comparacion;
			int _umbral;
			
			// funciones auxiliares
			
			int ValorDeElemento(const Elementos& elementos) const;
	};

	class Transicion
	{
		public:
			
			// Constructor
			Transicion(const string& id, const string& id_comportamiento_destino);
			
			// Destructor
			~Transicion();
			
			// Devuelve verdadero si dado un estado de elementos
			// se cumple la condicion de transicion.
			// Devuelve falso en caso contrario.
			bool SeCumple(const Elementos& elementos) const;
			
			// Ejecuta las actualizaciones que hace la transicion
			void EjecutarActualizaciones();
			
			const string& Id() const;
			
			// Devuelve el comportamiento al que cambia al ejecutarse
			const string& IdComportamientoDestino() const;
			
			// Sirven para construir la transicion a partir del xml
			void AgregarCondicion(const Condicion* condicion);
			void AgregarActualizacion(Actualizacion* actualizacion);
			
		private:
			
			const string _id;
			const string _id_comportamiento_destino;
			
			set<const Condicion*> _condiciones;
			set<Actualizacion*> _actualizaciones;
			
	};

	class Comportamiento
	{
		public:
			
			Comportamiento(const string& id);
			
			const string& Id() const;
			
			void EjecutarElementos();
			
			const EstadoDeActuadores* EstadoActuadores() const;
			
			// devuelve la primera transicion que este activa.
			// NULL si no se activa ninguna
			Transicion* TransicionActiva(Elementos& elementos);
			
			/* Escribe en el stream los ids
			 * de los distintos elementos */
			void LoguearEncabezadoDeElementos(ostream& log_stream);
			
			/* Escribe en el stream los estados
			 * de cada uno de los elementos */
			void LoguearEstadoDeElementos(ostream& log_stream);
			
			/* se usan para construir el comportamiento a partir del XML */
			void AgregarActuador(Actuador* actuador);
			void AgregarCaja(Caja* caja);
			void AgregarTransicion(Transicion* transicion);
			
			/* Para chequear que los sensores y actuadores
			 * coincidan con los de la RAL */
			bool ChequearActuadores(const ListaDeActuadores* actuadores) const;
			
			/* devuelve el elemento correspondiente al id
			 * o NULL en el caso que no exista.
			 * Sirve al resolver las entradas de cajas y actuadores
			 * cuando se construyen en el parser */
			Elemento* ElementoPorId(const string& id_elemento);
			
		private:
			
			string _id;
			
			map<const string,Caja*> _cajas;
			map<const string,Actuador*> _actuadores;
			
			set<Transicion*> _transiciones;
	};

	class Conducta
	{
		public:
			
			Conducta(ostream& log_stream);
			
			/* destruye los elementos de los maps */
			~Conducta();
			
			/* Actualiza el comportamiento actual */
			void Actualizar(const EstadoDeSensores* estado);
			
			/* se usan para construir la conducta a partir del XML */
			void AgregarComportamiento(Comportamiento* comportamiento);
			void AgregarTimer(Timer* timer);
			void AgregarContador(Contador* contador);
			void AgregarSensor(Sensor* sensor);
			
			/* setea el comportamiento inicial */
			void SetComportamientoInicial(const string& id);
			const EstadoDeActuadores* EstadoActuadores();
			
			/* Para chequear que los sensores y actuadores
			 * coincidan con los de la RAL.
			 * Devuelve false en el caso que no coincidan */
			bool ChequearSensores(const ListaDeSensores* sensores) const;
			bool ChequearActuadores(const ListaDeActuadores* actuadores) const;
			
			/* Escribe en el log_stream los ids
			 * de los distintos elementos */
			void LoguearEncabezadoDeElementos();
			
			/* Escribe en el log_stream los estados
			 * de cada uno de los elementos */
			void LoguearEstadoDeElementos();
			
			/* devuelve el elemento correspondiente al id
			 * o NULL en el caso que no exista.
			 * Sirve al resolver las entradas de cajas y actuadores
			 * cuando se construyen en el parser */
			Elemento* ElementoPorId(const string& id_elemento);
			
		private:
			
			Comportamiento* comportamiento_actual;
			
			map<const string,Sensor*> _sensores;
			map<const string,Timer*> _timers;
			map<const string,Contador*> _contadores;
			
			map<const string,Comportamiento*> _comportamientos;
			
			/* el log donde se va escribiendo el estado de los elementos
			 * en cada iteracion */
			ostream& _log_stream;
			
			/* Sirve para monitorear el tiempo de ejecucion */
			struct timeb _tiempo_inicio;
			bool _primera_medicion_tiempo;
			
			// funciones auxiliares
			
			void CambiarAComportamiento( const string& id );
			
			void ActualizarSensores(const EstadoDeSensores* estado_sensores);
			
			int TiempoTranscurrido();
			
			void EjecutarElementos();
	};

#endif // _ESTRUCTURAS_H_
