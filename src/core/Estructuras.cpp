#include "Estructuras.h"

void Error(const char* msg)
{
	cout << "Error: " << msg << ". Se suspende la ejecucion." << endl;
	exit(0);
}

/* Clase Elemento */

Elemento::Elemento(Elemento_t tipo, const string& id, int valor_inicial)
	: _valor(valor_inicial), _tipo(tipo), _id(id)
{
}

const string& Elemento::Id() const
{
	return _id;
}

int Elemento::GetValor() const
{
	return _valor;
}

/* Clase ElementoConEntradas */

ElementoConEntradas::ElementoConEntradas(Elemento_t tipo, const string& id, int valor_inicial)
	: Elemento(tipo,id,valor_inicial)
{
}

const set<Elemento*>& ElementoConEntradas::Entradas() const
{
	return _entradas;
}

bool ElementoConEntradas::TieneEntrada(Elemento* entrada) const
{
	forall(it,_entradas)
		if ( (*it)==entrada )
			return true;
	return false;
}

void ElementoConEntradas::AgregarEntrada(Elemento* entrada)
{
	pair<set<Elemento*>::iterator,bool> ret;
	ret = _entradas.insert( entrada );
	if (ret.second==false)
		Error("Caja::AgregarEntrada - se trato de agregar una entrada que ya existia");
}

/* Clase Sensor */

Sensor::Sensor(const string& id)
	: Elemento(TIPO_SENSOR,id,0)
{
}

void Sensor::ActualizarValor(int valor)
{
	_valor = valor;
}

int Sensor::Ejecutar()
{
	return _valor;
}

/* Clase Caja */

Caja::Caja(const string& id, const Punto punto_min, const Punto punto_max)
	: ElementoConEntradas(TIPO_CAJA,id,0), _punto_min(punto_min), _punto_max(punto_max)
{
}

Caja::~Caja()
{
}

int Caja::Ejecutar()
{
	int acumulador, resultado;
	
	acumulador = 0;
	forall(it,_entradas)
		acumulador += (*it)->GetValor();
	
	// acá calculo la función formada por los puntos
	// la ecuación de la recta es Y = a*(X - x0) + y0
	// donde a = (y1 - y0)/(x1 - x0)
	
	if ( acumulador <= _punto_min.x )
		resultado = _punto_min.y;
		
	else if ( _punto_max.x <= acumulador )
		resultado = _punto_max.y;
	
	else
	{
		float a = ( (float)_punto_max.y - (float)_punto_min.y ) - ( (float)_punto_max.x - (float)_punto_min.x );
		resultado = (int)( a*( (float)acumulador - (float)_punto_min.x ) + (float)_punto_min.y );
	}
	
	_valor = resultado;
	return _valor;
}

/* Clase Actuador */

Actuador::Actuador(const string& id)
	: ElementoConEntradas(TIPO_ACTUADOR,id,0)
{
}

int Actuador::Ejecutar()
{
	int acumulador;
	
	acumulador = 0;
	forall(it,_entradas)
		acumulador += (*it)->GetValor();
	
	_valor = acumulador;
	return _valor;
}

/* Clase Timer */

Timer::Timer(const string& id)
	: Elemento(TIPO_TIMER,id,0), _primera_medicion(true)
{
}

void Timer::Resetear()
{
	_valor=0;
	ftime(&_tiempo_inicio);
}

int Timer::Ejecutar()
{
	// diftime devuelve la diferencia de tiempo en segundos como double
	_valor = TiempoTranscurrido();
	
	return 0;
}

int Timer::TiempoTranscurrido()
{
	if (_primera_medicion)
	{
		ftime(&_tiempo_inicio);
		_primera_medicion = false;
	}
	
	struct timeb tiempo_actual;
	ftime(&tiempo_actual);
	return ((int)
	(
		1000.0 * 
		(tiempo_actual.time - _tiempo_inicio.time) +
		(tiempo_actual.millitm - _tiempo_inicio.millitm)
	)) / 1000;
}

/* Clase contador */

Contador::Contador(const string& id)
	: Elemento(TIPO_CONTADOR,id,0)
{
}

void Contador::Incrementar()
{
	_valor++;
}

void Contador::Decrementar()
{
	_valor--;
}

void Contador::Resetear()
{
	_valor = 0;
}

int Contador::Ejecutar()
{
	return 0;
}

/* Clase Conducta */

Conducta::Conducta(ostream& log_stream)
	: comportamiento_actual(NULL), _log_stream(log_stream), _primera_medicion_tiempo(true)
{
}

Conducta::~Conducta()
{
	forall(it,_comportamientos) delete it->second;
	forall(it,_contadores) delete it->second;
	forall(it,_timers) delete it->second;
	forall(it,_sensores) delete it->second;
}

void Conducta::Actualizar(const EstadoDeSensores* estado_sensores)
{
	if (!comportamiento_actual) Error("Conducta::Actualizar - No existe un comportamiento actual");
	
	ActualizarSensores(estado_sensores);
	
	Elementos elementos;
	elementos.timers = &_timers;
	elementos.contadores = &_contadores;
	elementos.sensores = &_sensores;
	
	Transicion* transicion_activa = comportamiento_actual->TransicionActiva(elementos);
	if ( transicion_activa )
	{
		transicion_activa->EjecutarActualizaciones();
		CambiarAComportamiento( transicion_activa->IdComportamientoDestino() );
	}
	
	EjecutarElementos();
	
	LoguearEstadoDeElementos();
}

void Conducta::AgregarComportamiento(Comportamiento* comportamiento)
{
	pair<map<string,Comportamiento*>::iterator,bool> ret;
	ret = _comportamientos.insert( pair<string,Comportamiento*>(comportamiento->Id(),comportamiento) );
	if (ret.second==false)
		Error("Conducta::AgregarComportamiento - se trato de insertar un comportamiento con un id ya existente");
}

void Conducta::AgregarTimer(Timer* timer)
{
	pair<map<string,Timer*>::iterator,bool> ret;
	ret = _timers.insert( pair<string,Timer*>(timer->Id(),timer) );
	if (ret.second==false)
		Error("Conducta::AgregarTimer - se trato de insertar un timer con un id ya existente");
}

void Conducta::AgregarContador(Contador* contador)
{
	pair<map<string,Contador*>::iterator,bool> ret;
	ret = _contadores.insert( pair<string,Contador*>(contador->Id(),contador) );
	if (ret.second==false)
		Error("Conducta::AgregarContador - se trato de insertar un contador con un id ya existente");
}

void Conducta::AgregarSensor(Sensor* sensor)
{
	pair<map<string,Sensor*>::iterator,bool> ret;
	ret = _sensores.insert( pair<string,Sensor*>(sensor->Id(),sensor) );
	if (ret.second==false)
		Error("Comportamiento::AgregarSensor - se trato de insertar un sensor con un id ya existente");
}

void Conducta::SetComportamientoInicial(const string& id)
{
	CambiarAComportamiento(id);
}

const EstadoDeActuadores* Conducta::EstadoActuadores()
{
	return comportamiento_actual->EstadoActuadores();
}

bool Conducta::ChequearSensores(const ListaDeSensores* sensores) const
{
	forall(it,_sensores)
	{
		bool checked = false;
		forall(it_ral,*sensores)
			if ( (it->second)->Id() == (*it_ral) )
			{
				checked = true;
				break;
			}
		
		if (!checked)
			return false;
	}
	return true;
}

bool Conducta::ChequearActuadores(const ListaDeActuadores* actuadores) const
{
	forall(it,_comportamientos)
		if ( !(it->second)->ChequearActuadores(actuadores) )
			return false;
	
	return true;
}

void Conducta::LoguearEncabezadoDeElementos()
{
	_log_stream << "timestamp, ";
	
	forall(it,_sensores)
		_log_stream << (it->second)->Id() << ", ";
	forall(it,_timers)
		_log_stream << (it->second)->Id() << ", ";
	forall(it,_contadores)
		_log_stream << (it->second)->Id() << ", ";
		
	comportamiento_actual->LoguearEncabezadoDeElementos(_log_stream);
	
	_log_stream << endl;
}

void Conducta::LoguearEstadoDeElementos()
{
	_log_stream << TiempoTranscurrido() << ", ";
	
	forall(it,_sensores)
		_log_stream << (it->second)->GetValor() << ", ";
	forall(it,_timers)
		_log_stream << (it->second)->GetValor() << ", ";
	forall(it,_contadores)
		_log_stream << (it->second)->GetValor() << ", ";
	
	comportamiento_actual->LoguearEstadoDeElementos(_log_stream);
	
	_log_stream << endl;
}

Elemento* Conducta::ElementoPorId(const string& id_elemento)
{
	forall(it,_sensores)
		if ( (it->second)->Id() == id_elemento ) return it->second;
	
	forall(it,_timers)
		if ( (it->second)->Id() == id_elemento ) return it->second;
	
	forall(it,_contadores)
		if ( (it->second)->Id() == id_elemento ) return it->second;
	
	forall(it,_comportamientos)
	{
		Elemento* elemento = (it->second)->ElementoPorId(id_elemento);
		if (elemento) return elemento;
	}
	
	return NULL;
}

bool Conducta::OrdenarElementosTopologicamente()
{
	bool no_hay_ciclos = true;
	forall(it,_comportamientos)
		no_hay_ciclos = no_hay_ciclos && it->second->OrdenarElementosTopologicamente();
	
	return no_hay_ciclos;
}

bool Conducta::LosIdsSonUnicos()
{
	set<const Elemento*> elementos;
	forall(it,_timers) elementos.insert(it->second);
	forall(it,_contadores) elementos.insert(it->second);
	forall(it,_sensores) elementos.insert(it->second);
	
	bool son_unicos = true;
	forall(it,_comportamientos)
		son_unicos = son_unicos && it->second->LosIdsSonUnicos(elementos);
	
	return son_unicos;
}

void Conducta::CambiarAComportamiento( const string& id )
{
	map<string,Comportamiento*>::iterator it;
	it = _comportamientos.find(id);
	if (it==_comportamientos.end())
		Error("Conducta::CambiarAComportamiento - No existe un comportamiento con este id");
	
	cout << "cambiando a comportamiento " << id << endl;
	
	comportamiento_actual = it->second;
	
	LoguearEncabezadoDeElementos();
}

void Conducta::ActualizarSensores(const EstadoDeSensores* estado_sensores)
{
	forall(it,_sensores)
	{
		bool found = false;
		
		forall(it_estado,*estado_sensores)
			if (it_estado->id==it->first)
			{
				found = true;
				(it->second)->ActualizarValor(it_estado->valor);
			}
		
		if (!found)
			Error("Conducta::ActualizarSensores - Se intento actualizar un sensor con id inexistente.");
	}
}

int Conducta::TiempoTranscurrido()
{
	if (_primera_medicion_tiempo)
	{
		ftime(&_tiempo_inicio);
		_primera_medicion_tiempo = false;
	}
	
	struct timeb tiempo_actual;
	ftime(&tiempo_actual);
	return (int)
	(
		1000.0 *
		(tiempo_actual.time - _tiempo_inicio.time) +
		(tiempo_actual.millitm - _tiempo_inicio.millitm)
	);
}

void Conducta::EjecutarElementos()
{
	forall(it,_sensores)
		(it->second)->Ejecutar();
	forall(it,_timers)
		(it->second)->Ejecutar();
	forall(it,_contadores)
		(it->second)->Ejecutar();
	
	// los elementos del comportamiento dependen de los valores
	// de otros elementos como entradas, por eso los ejecuto al final
	
	comportamiento_actual->EjecutarElementos();
}

/* Clase Comportamiento */

Comportamiento::Comportamiento(const string& id)
{
	_id = id;
}

const string& Comportamiento::Id() const
{
	return _id;
}

void Comportamiento::EjecutarElementos()
{
	// Se supone que se ejecutaron antes todos los elementos
	// que no tengan entradas y que puedan servir como entrada.
	forall(it,_elementos_en_orden_topologico)
		(*it)->Ejecutar();
}

const EstadoDeActuadores* Comportamiento::EstadoActuadores() const
{
	EstadoDeActuadores* estado_actuadores = new EstadoDeActuadores();
	Item item_actuador;
	
	forall(it,_actuadores)
	{
		item_actuador.id = (it->second)->Id();
		item_actuador.valor = (it->second)->GetValor();
		estado_actuadores->push_back(item_actuador);
	}
	
	return estado_actuadores;
}

Transicion* Comportamiento::TransicionActiva(Elementos& elementos)
{
	elementos.cajas = &_cajas;
	elementos.actuadores = &_actuadores;
	
	forall(it,_transiciones)
		if ( (*it)->SeCumple(elementos) )
			return (*it);
	
	return NULL;
}

void Comportamiento::LoguearEncabezadoDeElementos(ostream& log_stream)
{
	forall(it,_elementos_en_orden_topologico)
		log_stream << (*it)->Id() << ", ";
}

void Comportamiento::LoguearEstadoDeElementos(ostream& log_stream)
{
	forall(it,_cajas)
		log_stream << (it->second)->GetValor() << ", ";
	forall(it,_actuadores)
		log_stream << (it->second)->GetValor() << ", ";
}

void Comportamiento::AgregarActuador(Actuador* actuador)
{
	pair<map<string,Actuador*>::iterator,bool> ret;
	ret = _actuadores.insert( pair<string,Actuador*>(actuador->Id(),actuador) );
	if (ret.second==false)
		Error("Comportamiento::AgregarActuador - se trato de insertar un actuador con un id ya existente en _actuadores");
	
	AgregarElementoConEntradas(actuador);
}

void Comportamiento::AgregarCaja(Caja* caja)
{
	pair<map<string,Caja*>::iterator,bool> ret;
	ret = _cajas.insert( pair<string,Caja*>(caja->Id(),caja) );
	if (ret.second==false)
		Error("Comportamiento::AgregarCaja - se trato de insertar una caja con un id ya existente");
	
	AgregarElementoConEntradas(caja);
}

void Comportamiento::AgregarTransicion(Transicion* transicion)
{
	pair<set<Transicion*>::iterator,bool> ret;
	ret = _transiciones.insert( transicion );
	if (ret.second==false)
		Error("Comportamiento::AgregarTransicion - se trato de agregar una transicion que ya existia");
}

void Comportamiento::AgregarElementoConEntradas(ElementoConEntradas* elemento)
{
	pair<set<ElementoConEntradas*>::iterator,bool> ret;
	ret = _elementos.insert( elemento );
	if (ret.second==false)
		Error("Comportamiento::AgregarElementoConEntradas - se trato de insertar un actuador con un id ya existente en _elementos");
}

bool Comportamiento::OrdenarElementosTopologicamente()
{
	int size = _elementos.size();
	list<int> elementos_sin_entradas;
	_elementos_en_orden_topologico.clear();
	
	// armo un vector con las cajas
	ElementoConEntradas* vector_cajas[size];
	int k=0;
	forall(it,_elementos)
	{
		vector_cajas[k] = *it;
		k++;
	}
	
	// armo la matriz de aristas (n^3) :s
	vector<vector<bool> > aristas(size,vector<bool>(size,false));
	
	forn(i,size)
	{
		bool tiene_entradas = false;
		forn(j,size)
		{
			if ( i!=j && vector_cajas[i]->TieneEntrada(vector_cajas[j]) )
			{
				tiene_entradas = true;
				aristas[i][j] = true;
			}
		}
		
		if (!tiene_entradas)
			elementos_sin_entradas.push_back(i);
	}
	
	// Armo la lista
	while (!elementos_sin_entradas.empty())
	{
		int index = elementos_sin_entradas.front();
		elementos_sin_entradas.pop_front();
		
		_elementos_en_orden_topologico.push_back(vector_cajas[index]);
		
		// para cada elemento
		forn(i,size)
			// Si tenia como entrada a 'index'
			if ( aristas[i][index] )
			{
				// saco esa arista
				aristas[i][index] = false;
				
				// me fijo si le queda alguna entrada
				bool tiene_entradas = false;
				forn(j,size)
					if ( aristas[i][j] )
						tiene_entradas=true;
				
				// si no tiene entrada es un nuevo nodo para la lista
				if (!tiene_entradas)
					elementos_sin_entradas.push_back(i);
			}
	}
	
	// si pude cargar todos los elementos, esta perfecto, devuelvo true
	// si no, el grafo tenia ciclos y devuelvo false
	return (size==(int)_elementos_en_orden_topologico.size());
}

bool Comportamiento::ChequearActuadores(const ListaDeActuadores* actuadores) const
{
	forall(it,_actuadores)
	{
		bool checked = false;
		forall(it_ral,*actuadores)
			if ( (it->second)->Id() == (*it_ral) )
			{
				checked=true;
				break;
			}
		if (!checked)
			return false;
	}
	return true;
}

Elemento* Comportamiento::ElementoPorId(const string& id_elemento)
{
	forall(it,_cajas)
		if ( (it->second)->Id() == id_elemento ) return it->second;
	
	forall(it,_actuadores)
		if ( (it->second)->Id() == id_elemento ) return it->second;
	
	return NULL;
}

bool Comportamiento::LosIdsSonUnicos(set<const Elemento*> elementos) const
{
	forall(it,_cajas) elementos.insert(it->second);
	forall(it,_actuadores) elementos.insert(it->second);
	
	forall(it,elementos)
		forall(it2,elementos)
			if ( it!=it2 && (*it)->Id()==(*it2)->Id() )
				return false;
	return true;
}

/* Clase Transicion */

Transicion::Transicion(const string& id, const string& id_comportamiento_destino)
	: _id(id), _id_comportamiento_destino(id_comportamiento_destino)
{
}

Transicion::~Transicion()
{
	forall(it,_actualizaciones) delete (*it);
}

bool Transicion::SeCumple(const Elementos& elementos) const
{
	forall(it,_condiciones)
		if ( !((*it)->SeCumple(elementos)) )
			return false;
	return true;
}

void Transicion::EjecutarActualizaciones()
{
	forall(it,_actualizaciones)
		(*it)->Ejecutar();
}

const string& Transicion::Id() const
{
	return _id;
}

const string& Transicion::IdComportamientoDestino() const
{
	return _id_comportamiento_destino;
}

void Transicion::AgregarCondicion(const Condicion* condicion)
{
	pair<set<const Condicion*>::iterator,bool> ret;
	ret = _condiciones.insert(condicion);
	if (ret.second==false)
		Error("Transicion::AgregarCondicion - se trato de agregar una condicion ya existente");
}

void Transicion::AgregarActualizacion(/*const */Actualizacion* actualizacion)
{
	pair<set<Actualizacion*>::iterator,bool> ret;
	ret = _actualizaciones.insert(actualizacion);
	if (ret.second==false)
		Error("Transicion::AgregarActualizacion - se trato de agregar una actualizacion ya existente");
}

/* Clase Condicion */

Condicion::Condicion(const string& id_elemento, Comparacion_t comparacion, int umbral)
	: _id_elemento(id_elemento), _comparacion(comparacion), _umbral(umbral)
{
}

bool Condicion::SeCumple(const Elementos& elementos) const
{
	int valor_elemento = ValorDeElemento(elementos);
	
	switch(_comparacion)
	{
		case IGUAL:
			return valor_elemento == _umbral;
			break;
		case MENOR:
			return valor_elemento < _umbral;
			break;
		case MENOR_IGUAL:
			return valor_elemento <= _umbral;
			break;
		case MAYOR:
			return valor_elemento > _umbral;
			break;
		case MAYOR_IGUAL:
			return valor_elemento >= _umbral;
			break;
		default:
			Error("Condicion::SeCumple - tipo de comparacion invalido");
			break;
	}
	
	// nunca llega aca. Es para que no se queje el compilador.
	return false;
}

int Condicion::ValorDeElemento(const Elementos& elementos) const
{
	forall(it,(*elementos.cajas))
		if ( (it->second)->Id() == _id_elemento )
			return (it->second)->GetValor();
	forall(it,(*elementos.sensores))
		if ( (it->second)->Id() == _id_elemento )
			return (it->second)->GetValor();
	forall(it,(*elementos.actuadores))
		if ( (it->second)->Id() == _id_elemento )
			return (it->second)->GetValor();
	forall(it,(*elementos.timers))
		if ( (it->second)->Id() == _id_elemento )
			return (it->second)->GetValor();
	forall(it,(*elementos.contadores))
		if ( (it->second)->Id() == _id_elemento )
			return (it->second)->GetValor();
	
	Error("Condicion::ValorDeElemento - El id no corresponde a ningun elemento.");
	
	// nunca llega, pero es para que el compilador no chille
	return -1;
}

/* Clase Actualizacion (interfaz) */

Actualizacion::Actualizacion()
{
}

/* Clase ActualizacionDeTimer */

ActualizacionDeTimer::ActualizacionDeTimer(Timer* timer)
	: _timer(timer)
{
}

void ActualizacionDeTimer::Ejecutar()
{
	_timer->Resetear();
}

/* Clase ActualizacionDeContador */

ActualizacionDeContador::ActualizacionDeContador(Contador* contador, Accion_t accion)
	: _contador(contador), _accion(accion)
{
}

void ActualizacionDeContador::Ejecutar()
{
	switch (_accion)
	{
		case RESETEAR:
			_contador->Resetear();
			break;
		case INCREMENTAR:
			_contador->Incrementar();
			break;
		case DECREMENTAR:
			_contador->Decrementar();
			break;
		default:
			Error("ActualizacionDeContador::Actualizar - tipo de accion invalida");
			break;
	}
}

