#include "estructuras.h"

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

int Elemento::SetValor(int valor)
{
	_valor = valor;
	return _valor;
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
	: Elemento(TIPO_TIMER,id,0)
{
	_initial_time = std::time(NULL);
}

void Timer::Resetear()
{
	_initial_time = std::time(NULL);
	_valor=0;
}

int Timer::Ejecutar()
{
	std::time_t current_time;
	std::time(&current_time);
	
	if ( current_time-_initial_time >= 1.0 )
	{
		_valor++;
		_initial_time += 1.0;
	}
	
	return 0;
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

Conducta::Conducta() : comportamiento_actual(NULL)
{
}

Conducta::~Conducta()
{
	forall(it,_comportamientos) delete it->second;
	forall(it,_contadores) delete it->second;
	forall(it,_timers) delete it->second;
	forall(it,_sensores) delete it->second;
}

void Conducta::Actualizar(const EstadoDeSensores& estado_sensores)
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
	
	forall(it,_timers)
		(it->second)->Ejecutar();
	
	/*
	 * @tfischer: lo siguiente esta por completitud,
	 * por si en algun momento se usa,
	 * pero esta comentado por performance
	 * 
	 * 
	
	forall(it,_contadores)
		(it->second)->Ejecutar();
	
	forall(it,_sensores)
		(it->second)->Ejecutar();
	
	*/
	
	comportamiento_actual->EjecutarElementos();
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

EstadoDeActuadores Conducta::EstadoActuadores()
{
	return comportamiento_actual->EstadoActuadores();
}

bool Conducta::ChequearSensores(const ListaDeSensores& sensores) const
{
	forall(it,_sensores)
	{
		bool checked = false;
		forall(it_ral,sensores)
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

bool Conducta::ChequearActuadores(const ListaDeActuadores& actuadores) const
{
	forall(it,_comportamientos)
		if ( !(it->second)->ChequearActuadores(actuadores) )
			return false;
	
	return true;
}

void Conducta::LoguearEncabezadoDeElementos(ostream& log_os)
{
	// TODO
}

void Conducta::LoguearEstadoDeElementos(ostream& log_os)
{
	// TODO
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

void Conducta::CambiarAComportamiento( const string& id )
{
	map<string,Comportamiento*>::iterator it;
	it = _comportamientos.find(id);
	if (it==_comportamientos.end())
		Error("Conducta::CambiarAComportamiento - No existe un comportamiento con este id");
	
	cout << "cambiando a comportamiento " << id << endl;
	
	comportamiento_actual = it->second;
}

void Conducta::ActualizarSensores(const EstadoDeSensores& estado_sensores)
{
	forall(it,_sensores)
	{
		bool found = false;
		
		forall(it_estado,estado_sensores)
			if (it_estado->id==it->first)
			{
				found = true;
				(it->second)->SetValor(it_estado->valor);
			}
		
		if (!found)
			Error("Conducta::ActualizarSensores - Se intento actualizar un sensor con id inexistente.");
	}
}

/* Clase Comportamiento */

Comportamiento::Comportamiento(const string& id)
{
	_id = id;
}

string Comportamiento::Id()
{
	return _id;
}

void Comportamiento::EjecutarElementos()
{
        forall(it,_actuadores)
                (it->second)->Ejecutar();
        forall(it,_cajas)
                (it->second)->Ejecutar();
}

EstadoDeActuadores Comportamiento::EstadoActuadores()
{
	// TODO: es medio turbio generar esto cada vez o?
	// sobre todo, quien se encarga de destruir la lista?
	EstadoDeActuadores estado_actuadores;
	Item item_actuador;
	
	forall(it,_actuadores)
	{
		item_actuador.id = (it->second)->Id();
		item_actuador.valor = (it->second)->GetValor();
		estado_actuadores.push_back(item_actuador);
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

void Comportamiento::AgregarActuador(Actuador* actuador)
{
	pair<map<string,Actuador*>::iterator,bool> ret;
	ret = _actuadores.insert( pair<string,Actuador*>(actuador->Id(),actuador) );
	if (ret.second==false)
		Error("Comportamiento::AgregarActuador - se trato de insertar un actuador con un id ya existente");
}

void Comportamiento::AgregarCaja(Caja* caja)
{
	pair<map<string,Caja*>::iterator,bool> ret;
	ret = _cajas.insert( pair<string,Caja*>(caja->Id(),caja) );
	if (ret.second==false)
		Error("Comportamiento::AgregarCaja - se trato de insertar una caja con un id ya existente");
}

void Comportamiento::AgregarTransicion(Transicion* transicion)
{
	pair<set<Transicion*>::iterator,bool> ret;
	ret = _transiciones.insert( transicion );
	if (ret.second==false)
		Error("Comportamiento::AgregarTransicion - se trato de agregar una transicion que ya existia");
}

bool Comportamiento::ChequearActuadores(const ListaDeActuadores& actuadores) const
{
	forall(it,_actuadores)
	{
		bool checked = false;
		forall(it_ral,actuadores)
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

const string& Transicion::IdComportamientoDestino()
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

