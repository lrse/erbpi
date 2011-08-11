#ifndef _ESTRUCTURAS_H_
#define _ESTRUCTURAS_H_

	// incluir las cosas estandard
	#include <string>
	#include <vector>
	using namespace std;

	// definicion de tipos de elementos
	#define TIPO_SENSOR 0
	#define TIPO_CAJA 1
	#define TIPO_ACTUADOR 2
	
	struct Punto {
      int x;
      int y;
	};

	class Elemento {
		public:
			~Elemento();				// destructor
			string _id;					// es el id
			int _valor;					// es el valor de salida
			int _entrada;				// es la sumatoria de todas sus entradas
			int _tipo;					// tipo: sensor, caja o actuador
			string getId();				// devuelve _id
			int setValor(int);			// setea _valor y lo devuelve
			int getValor();				// devuelve _valor
			int getEntrada();			// devuelve _entrada
			virtual int ejecutar();		// es virtual, llama a la de la clase hija...
	};

	class Sensor : public Elemento {		// hereda de la clase Elemento
		public:
			Sensor();					// constructor
			Sensor(string);				// constructor
			~Sensor();					// destructor
			int ejecutar();				// devuelve Elemento::_valor
	};

	class Caja : public Elemento {		// hereda de la clase Elemento
		public:
			Caja();						// constructor
			Caja(string);				// constructor
			~Caja();					// destructor
			vector<Elemento*>* _entradas;
			vector<Punto>* _puntos;
			int ejecutar();				// para cada "i" en _entradas, acumula *(_entradas[i]).getValor(), realiza resultado = función(acumulador), y luego, setea el "resultado" en Elemento::_valor, setea el "acumulador" en Elemento::_entrada y devuelve Elmemento::_valor
	};

	class Actuador : public Elemento {	// hereda de la clase Elemento
		public:
			Actuador();					// constructor
			Actuador(string);			// constructor
			~Actuador();				// destructor
			vector<Elemento*>* _entradas;
			int ejecutar();				// para cada "i" en _entradas, acumula *(_entradas[i]).getValor(), luego setea el acumulador en Elemento::_valor y devuelve Elmemento::_valor
	};
	
#endif // _ESTRUCTURAS_H_
