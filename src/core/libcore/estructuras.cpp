#include "estructuras.h"

Elemento::~Elemento(){ }

string Elemento::getId(){
	return (this->_id);
}

int Elemento::setValor(int valor){
	(this->_valor) = valor;
	return (this->_valor);
}

int Elemento::getValor(){
	return (this->_valor);
}

int Elemento::getEntrada(){
	return (this->_entrada);
}

int Elemento::ejecutar(){ }

Sensor::Sensor(){ }

Sensor::Sensor(string id){
	(this->_id) = id;
	(this->_valor) = 0;	
	(this->_tipo) = TIPO_SENSOR;
}

Sensor::~Sensor(){ }

int Sensor::ejecutar(){
	return (this->_valor);
}

Caja::Caja(){ }

Caja::Caja(string id){
	(this->_id) = id;
	(this->_valor) = 0;
	(this->_tipo) = TIPO_CAJA;
	(this->_entrada) = 0;		
	_entradas = new vector<Elemento*>;
	_puntos = new vector<Punto>;
}

Caja::~Caja(){ }

int Caja::ejecutar(){
	int i, acumulador, resultado;
	acumulador = 0;
	for( i = 0; i < (*_entradas).size(); i++ ){ // para cada "i" en _entradas acumula *(_entradas[i]).getValor()
		acumulador += ((*_entradas)[i])->getValor();
	}
	// acá calculo la función formada por los puntos // la ecuación de la recta es Y = (((y0 - y1)/(x0 - x1))*(X - x0)) + y0
	if ( acumulador <= ((*_puntos)[0]).x )
		resultado = ((*_puntos)[0]).y;
		else if ( acumulador >= ((*_puntos)[1]).x )
				resultado = ((*_puntos)[1]).y;
				//else resultado = (int)( ( ((float)(( ((*_puntos)[0]).y - ((*_puntos)[1]).y )/( ((*_puntos)[0]).x - ((*_puntos)[1]).x )))*(acumulador - ((*_puntos)[0]).x)) + ((*_puntos)[0]).y );
				else resultado = (int)( (int)(( ((float)( ((float)( ((*_puntos)[0]).y - ((*_puntos)[1]).y )) / ((float)( ((*_puntos)[0]).x - ((*_puntos)[1]).x )) )) * (acumulador - ((*_puntos)[0]).x)) ) + ((*_puntos)[0]).y );
	(this->_entrada) = acumulador;	// setea el Elemento::_entrada
	(this->_valor) = resultado;		// setea el Elemento::_valor
	return (this->_valor);			// devuelvo el Elemento::_valor
}

Actuador::Actuador(){ }

Actuador::Actuador(string id){
	(this->_id) = id;
	(this->_valor) = 0;
	(this->_tipo) = TIPO_ACTUADOR;		
	_entradas = new vector<Elemento*>;
}

Actuador::~Actuador(){ }

int Actuador::ejecutar(){
	int i, acumulador;
	acumulador = 0;
	for( i = 0; i < (*_entradas).size(); i++ ){ // para cada "i" en _entradas acumula *(_entradas[i]).getValor()
		acumulador += ((*_entradas)[i])->getValor();
	}
	(this->_valor) = acumulador;	// setea el Elemento::_valor
	return (this->_valor); 			// devuelvo el Elemento::_valor
}
