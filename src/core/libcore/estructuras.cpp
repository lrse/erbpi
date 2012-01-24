#include "estructuras.h"

void Error(const char* msg)
{
    cout << "Error: " << msg << ". Se suspende la ejecucion." << endl;
    exit(0);
}

/* ---------------------------------------------------------------------
 * ELEMENTO
 * ------------------------------------------------------------------ */

Elemento::Elemento(Elemento_t tipo, const string& id, const string& descripcion, int valor_inicial)
    : _valor(valor_inicial),
      _tipo(tipo),
      _valor_inicial(valor_inicial), 
      _id(id),
      _descripcion(descripcion) 
{
}

const string& Elemento::GetId() const
{
    return _id;
}

const string& Elemento::GetDescripcion() const
{
    return _descripcion;
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

void Elemento::Resetear()
{
    _valor = _valor_inicial;
}

/* ---------------------------------------------------------------------
 * ELEMENTO CON ENTRADAS
 * ------------------------------------------------------------------ */

ElementoConEntradas::ElementoConEntradas(Elemento_t tipo, const string& id, const string& descripcion, int valor_inicial)
    : Elemento(tipo,id,descripcion,valor_inicial)
{
}

void ElementoConEntradas::AgregarEntrada(const Elemento* entrada)
{
    pair<set<const Elemento*>::iterator,bool> ret;
    ret = _entradas.insert( entrada );
    if (ret.second==false)
        Error("Caja::AgregarEntrada - se trato de agregar una entrada que ya existia");
}

void ElementoConEntradas::LimpiarEntradas()
{
    _entradas.clear();
}

/* ---------------------------------------------------------------------
 * SENSOR
 * ------------------------------------------------------------------ */

Sensor::Sensor(const string& id, const string& descripcion)
    : Elemento(TIPO_SENSOR,id,descripcion,0)
{
}

int Sensor::Ejecutar()
{
    return _valor;
}

/* ---------------------------------------------------------------------
 * CAJA
 * ------------------------------------------------------------------ */

Caja::Caja(const string& id, const string& descripcion, const Punto punto_min, const Punto punto_max)
    : ElementoConEntradas(TIPO_CAJA,id,descripcion,0),
      _punto_min(punto_min),
      _punto_max(punto_max)
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
    
//    cout << "nuevo valor de " << GetDescripcion() << ": " << GetValor() << endl;
    
    return _valor;
}

/* ---------------------------------------------------------------------
 * ACTUADOR
 * ------------------------------------------------------------------ */

Actuador::Actuador(const string& id, const string& descripcion)
    : ElementoConEntradas(TIPO_ACTUADOR,id,descripcion,0)
{
}

int Actuador::Ejecutar()
{
    int acumulador;
    
    acumulador = 0;
    forall(it,_entradas)
        acumulador += (*it)->GetValor();
    
    _valor = acumulador;
    
//    cout << "nuevo valor de " << GetDescripcion() << " con " << _entradas.size() << " entradas: " << GetValor() << endl;
    
    return _valor;
}

/* ---------------------------------------------------------------------
 * TIMER
 * ------------------------------------------------------------------ */

Timer::Timer(const string& id, const string& descripcion)
    : Elemento(TIPO_TIMER,id,descripcion,0)
{
    _initial_time = std::time(NULL);
}

void Timer::Resetear()
{
    _initial_time = std::time(NULL);
    
    Elemento::Resetear();
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

/* ---------------------------------------------------------------------
 * CONTADOR
 * ------------------------------------------------------------------ */

Contador::Contador(const string& id, const string& descripcion)
    : Elemento(TIPO_CONTADOR,id,descripcion,0)
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

int Contador::Ejecutar()
{
    return 0;
}

/* ---------------------------------------------------------------------
 * CONDUCTA
 * ------------------------------------------------------------------ */

Conducta::Conducta()
    : comportamiento_actual(NULL)
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
    if (!comportamiento_actual)
        Error("Conducta::Actualizar - No existe un comportamiento actual");
    
    ActualizarSensores(estado_sensores);
    
    Elementos elementos;
    elementos.timers = &_timers;
    elementos.contadores = &_contadores;
    elementos.sensores = &_sensores;
    elementos.actuadores = &_actuadores;
    
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
    
    forall(it,_actuadores)
        (it->second)->Ejecutar();
}

void Conducta::AgregarComportamiento(Comportamiento* comportamiento)
{
    pair<map<string,Comportamiento*>::iterator,bool> ret;
    ret = _comportamientos.insert( pair<string,Comportamiento*>(comportamiento->GetId(),comportamiento) );
    if (ret.second==false)
        Error("Conducta::AgregarComportamiento - se trato de insertar un comportamiento con un id ya existente");
}

void Conducta::AgregarTimer(Timer* timer)
{
    pair<map<string,Timer*>::iterator,bool> ret;
    ret = _timers.insert( pair<string,Timer*>(timer->GetId(),timer) );
    if (ret.second==false)
        Error("Conducta::AgregarTimer - se trato de insertar un timer con un id ya existente");
}

void Conducta::AgregarContador(Contador* contador)
{
    pair<map<string,Contador*>::iterator,bool> ret;
    ret = _contadores.insert( pair<string,Contador*>(contador->GetId(),contador) );
    if (ret.second==false)
        Error("Conducta::AgregarContador - se trato de insertar un contador con un id ya existente");
}

void Conducta::AgregarSensor(Sensor* sensor)
{
    pair<map<string,Sensor*>::iterator,bool> ret;
    ret = _sensores.insert( pair<string,Sensor*>(sensor->GetId(),sensor) );
    if (ret.second==false)
        Error("Comportamiento::AgregarSensor - se trato de insertar un sensor con un id ya existente");
}

void Conducta::AgregarActuador(Actuador* actuador)
{
    cout << "Agregando actuador " << actuador->GetId() << endl;
    pair<map<string,Actuador*>::iterator,bool> ret;
    ret = _actuadores.insert( pair<string,Actuador*>(actuador->GetId(),actuador) );
    if (ret.second==false)
        Error("Conducta::AgregarActuador - se trato de insertar un actuador con un id ya existente");
}

void Conducta::SetComportamientoInicial(const string& id)
{
    CambiarAComportamiento(id);
}

EstadoDeActuadores Conducta::EstadoActuadores()
{
    // TODO: es medio turbio generar esto cada vez o?
    // sobre todo, quien se encarga de destruir la lista?
    EstadoDeActuadores estado_actuadores;
    Item item_actuador;
    
    forall(it,_actuadores)
    {
        item_actuador.id = (it->second)->GetId();
        item_actuador.valor = (it->second)->GetValor();
        estado_actuadores.push_back(item_actuador);
    }
    
    return estado_actuadores;
}

bool Conducta::ChequearSensores(const ListaDeSensores& sensores) const
{
    forall(it,_sensores)
    {
        bool checked = false;
        forall(it_ral,sensores){
            //cout << (it->second)->Id() << " = " << (*it_ral) << " ?" << endl;
            if ( (it->second)->GetId() == (*it_ral) )
            {
                checked = true;
                break;
            }
        }
        
        if (!checked)
            return false;
    }
    return true;
}

bool Conducta::ChequearActuadores(const ListaDeActuadores& actuadores) const
{
    forall(it,_actuadores)
    {
        bool checked = false;
        forall(it_ral,actuadores){
            //cout << (it->second)->Id() << " = " << (*it_ral) << " ?" << endl;
            if ( (it->second)->GetId() == (*it_ral) )
            {
                checked=true;
                break;
            }
        }
        if (!checked)
        {
            cout << "Error: El actuador " << (it->second)->GetId() << " no esta definido en la RAL." << endl;
            return false;
        }
    }
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
        if ( (it->second)->GetId() == id_elemento ) return it->second;
    
    forall(it,_timers)
        if ( (it->second)->GetId() == id_elemento ) return it->second;
    
    forall(it,_contadores)
        if ( (it->second)->GetId() == id_elemento ) return it->second;
    
    forall(it,_actuadores)
        if ( (it->second)->GetId() == id_elemento ) return it->second;
    
    forall(it,_comportamientos)
    {
        Elemento* elemento = (it->second)->ElementoPorId(id_elemento);
        if (elemento) return elemento;
    }
    
    return NULL;
}

Comportamiento* Conducta::ComportamientoPorId(const string& id_comportamiento)
{
    map<string,Comportamiento*>::iterator it;
    it = _comportamientos.find(id_comportamiento);
    
    if (it==_comportamientos.end())
        return NULL;

    return it->second;
}

void Conducta::CambiarAComportamiento( const string& id )
{
    Comportamiento* proximo_comportamiento = ComportamientoPorId(id);
    
    if (!proximo_comportamiento)
        Error("Conducta::CambiarAComportamiento - No existe un comportamiento con este id");
    
    cout << "cambiando a comportamiento " << proximo_comportamiento->GetDescripcion() << endl;
    
    if ( comportamiento_actual )
    {
        comportamiento_actual->LimpiarConecciones();
        
        forall(it,_actuadores)
        {
            (it->second)->LimpiarEntradas();
            (it->second)->Resetear();
        }
    }
    
    proximo_comportamiento->InicializarConecciones();
    
    comportamiento_actual = proximo_comportamiento;
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

/* ---------------------------------------------------------------------
 * CONECCION
 * ------------------------------------------------------------------ */

Coneccion::Coneccion(const Elemento* origen, ElementoConEntradas* destino)
    : _origen(origen), 
      _destino(destino)
{
}

const Elemento* Coneccion::GetOrigen() const
{
    return _origen;
}

ElementoConEntradas* Coneccion::GetDestino() const
{
    return _destino;
}

/* ---------------------------------------------------------------------
 * COMPORTAMIENTO
 * ------------------------------------------------------------------ */

Comportamiento::Comportamiento(const string& id, const string& descripcion)
    : _id(id),
      _descripcion(descripcion)
{
}

const string& Comportamiento::GetId()
{
    return _id;
}

const string& Comportamiento::GetDescripcion()
{
    return _descripcion;
}

void Comportamiento::EjecutarElementos()
{
    forall(it,_cajas)
        (it->second)->Ejecutar();
}

Transicion* Comportamiento::TransicionActiva(Elementos& elementos)
{
    elementos.cajas = &_cajas;
    
    forall(it,_transiciones)
        if ( (*it)->SeCumple(elementos) )
            return (*it);
    
    return NULL;
}

void Comportamiento::AgregarCaja(Caja* caja)
{
    pair<map<string,Caja*>::iterator,bool> ret;
    ret = _cajas.insert( pair<string,Caja*>(caja->GetId(),caja) );
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

void Comportamiento::AgregarConeccion(Coneccion* coneccion)
{
    pair<set<Coneccion*>::iterator,bool> ret;
    ret = _conecciones.insert( coneccion );
    if (ret.second==false)
        Error("Comportamiento::AgregarConeccion - se trato de agregar una coneccion que ya existia");
}

Elemento* Comportamiento::ElementoPorId(const string& id_elemento)
{
    forall(it,_cajas)
        if ( (it->second)->GetId() == id_elemento ) return it->second;
    
    return NULL;
}

void Comportamiento::InicializarConecciones()
{
    forall(it,_conecciones)
        (*it)->GetDestino()->AgregarEntrada((*it)->GetOrigen());
}

void Comportamiento::LimpiarConecciones()
{
    forall(it,_cajas)
        (it->second)->LimpiarEntradas();
}

/* ---------------------------------------------------------------------
 * TRANSICION
 * ------------------------------------------------------------------ */

Transicion::Transicion(const string& id, const string& id_comportamiento_destino)
    : _id(id),
      _id_comportamiento_destino(id_comportamiento_destino)
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

/* ---------------------------------------------------------------------
 * CONDICION
 * ------------------------------------------------------------------ */

Condicion::Condicion(const string& id_elemento, Comparacion_t comparacion, int umbral)
    : _id_elemento(id_elemento),
      _comparacion(comparacion),
      _umbral(umbral)
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
        if ( (it->second)->GetId() == _id_elemento )
            return (it->second)->GetValor();
    forall(it,(*elementos.sensores))
        if ( (it->second)->GetId() == _id_elemento )
            return (it->second)->GetValor();
    forall(it,(*elementos.actuadores))
        if ( (it->second)->GetId() == _id_elemento )
            return (it->second)->GetValor();
    forall(it,(*elementos.timers))
        if ( (it->second)->GetId() == _id_elemento )
            return (it->second)->GetValor();
    forall(it,(*elementos.contadores))
        if ( (it->second)->GetId() == _id_elemento )
            return (it->second)->GetValor();
    
    Error("Condicion::ValorDeElemento - El id no corresponde a ningun elemento.");
    
    // nunca llega, pero es para que el compilador no chille
    return -1;
}

/* ---------------------------------------------------------------------
 * ACTUALIZACION (INTERFAZ)
 * ------------------------------------------------------------------ */

Actualizacion::Actualizacion()
{
}

/* ---------------------------------------------------------------------
 * ACTUALIZACION DE TIMER
 * ------------------------------------------------------------------ */

ActualizacionDeTimer::ActualizacionDeTimer(Timer* timer)
    : _timer(timer)
{
}

void ActualizacionDeTimer::Ejecutar()
{
    _timer->Resetear();
}

/* ---------------------------------------------------------------------
 * ACTUALIZACION DE CONTADOR
 * ------------------------------------------------------------------ */

ActualizacionDeContador::ActualizacionDeContador(Contador* contador, Accion_t accion)
    : _contador(contador),
      _accion(accion)
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

