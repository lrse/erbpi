#ifndef _ESTRUCTURAS_H_
#define _ESTRUCTURAS_H_

    // incluir las cosas estandard
    #include <set>
    #include <map>
    #include <ctime>
    #include <string>
    #include <vector>
    #include <iostream>
    #include <stdlib.h>
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
            
            // Destructor virtual para que al invocar el destructor 
            // sobre una clase base (A* p, delete p), 
            // se invoque el constructor de la clase real de p (~B)
            // y no ~A, cuando B deriva de A.
            virtual ~Elemento() { } ;
            
            int SetValor(int);
            
            int GetValor() const;
            
            const string& GetId() const;
            
            const string& GetDescripcion() const;
            
            virtual void Resetear();
            
            // es implementado por cada clase hija
            virtual int Ejecutar() = 0;
            
        protected:
            
            // esto la hace una clase abstracta
            Elemento(Elemento_t tipo, const string& id, const string& descripcion, int valor_inicial = 0);
            
            int _valor;
            
        private:
            
            Elemento_t _tipo;
            
            int _valor_inicial;
            
            const string _id;
            
            const string _descripcion;
    };

    class ElementoConEntradas : public Elemento
    {
        public:
            
            /* agrega una entrada a la caja.
             * Se usa al construir la caja a partir del xml */
            void AgregarEntrada(const Elemento* entrada);
            
            /* Se usa cuando al cambiar de comportamiento,
             * las entradas pasan a ser distintas */
            void LimpiarEntradas();
            
        protected:
            
            // esto la hace una clase abstracta
            ElementoConEntradas(Elemento_t tipo, const string& id, const string& descripcion, int valor_inicial = 0);
            
            set<const Elemento*> _entradas;
            
    };

    class Sensor : public Elemento
    {
        public:
            
            Sensor(const string& id, const string& descripcion);
            
            int Ejecutar();
    };

    class Caja : public ElementoConEntradas
    {
        public:
            
            Caja(const string& id, const string& descripcion, const Punto punto_min, const Punto punto_max);
            
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
            
            Actuador(const string& id, const string& descripcion);
            
            int Ejecutar();
    };

    class Timer : public Elemento
    {
        public:
            Timer(const string& id, const string& descripcion);
            void Resetear();
            int Ejecutar();
        private:
            std::time_t _initial_time;
    };

    class Contador : public Elemento
    {
        public:
            Contador(const string& id, const string& descripcion);
            void Incrementar();
            void Decrementar();
            int Ejecutar();
        // TODO: Estaria bueno hacer un override de SetValor()
        // que tire error asi solo se modifica como es debido
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
            
            // Destructor virtual para que al invocar el destructor 
            // sobre una clase base (A* p, delete p), 
            // se invoque el constructor de la clase real de p (~B)
            // y no ~A, cuando B deriva de A.
            virtual ~Actualizacion() { } ;
            
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
            const string& IdComportamientoDestino();
            
            // Sirven para construir la transicion a partir del xml
            void AgregarCondicion(const Condicion* condicion);
            // TODO: ver si se puede hacer const
            void AgregarActualizacion(/*const */Actualizacion* actualizacion);
            
        private:
            
            const string _id;
            const string _id_comportamiento_destino;
            
            set<const Condicion*> _condiciones;
            // TODO: ver si se puede hacer const
            set</*const */Actualizacion*> _actualizaciones;
            
    };

    class Coneccion
    {
        public:
            
            Coneccion(const Elemento* origen, ElementoConEntradas* destino);
            
            const Elemento* GetOrigen() const;
            
            ElementoConEntradas* GetDestino() const;
            
        private:
            
            const Elemento* _origen;
            
            ElementoConEntradas* _destino;
    };

    class Comportamiento
    {
        public:
            
            Comportamiento(const string& id, const string& descripcion);
            
            /* Devuelve el identificador unico del comportamiento */
            const string& GetId();
            
            /* Devuelve el nombre declarativo del comportamiento */
            const string& GetDescripcion();
            
            /* Actualiza los elementos locales (cajas y actuadores) */
            void EjecutarElementos();
            
            /* se usan para construir el comportamiento 
             * a partir del XML */
            void AgregarCaja(Caja* caja);
            
            void AgregarTransicion(Transicion* transicion);
            
            void AgregarConeccion(Coneccion* coneccion);
            
            /* devuelve el elemento correspondiente al id
             * o NULL en el caso que no exista.
             * Sirve al resolver las entradas de cajas y actuadores
             * cuando se construyen en el parser */
            Elemento* ElementoPorId(const string& id_elemento);
            
            /* devuelve la primera transicion que este activa. 
             * NULL si no se activa ninguna */
            Transicion* TransicionActiva(Elementos& elementos);
            
            /* Inicializa las conecciones entre elementos 
             * para este comportamiento */
            void InicializarConecciones();
            
            /* Limpia las conecciones de los elementos 
             * porque no se usan mas 
             * al cambiar a otro comportamiento */
            void LimpiarConecciones();
            
        private:
            
            string _id;
            
            string _descripcion;
            
            map<const string,Caja*> _cajas;
            
            set<Coneccion*> _conecciones;
            
            set<Transicion*> _transiciones;
    };

    class Conducta
    {
        public:
            
            Conducta();
            
            /* destruye los elementos de los maps */
            ~Conducta();
            
            /* Actualiza el comportamiento actual */
            void Actualizar(const EstadoDeSensores& estado);
            
            /* se usan para construir la conducta a partir del XML */
            void AgregarComportamiento(Comportamiento* comportamiento);
            void AgregarTimer(Timer* timer);
            void AgregarContador(Contador* contador);
            void AgregarSensor(Sensor* sensor);
            void AgregarActuador(Actuador* actuador);
            
            /* setea el comportamiento inicial */
            void SetComportamientoInicial(const string& id);
            EstadoDeActuadores EstadoActuadores();
            
            /* Para chequear que los sensores y actuadores
             * coincidan con los de la RAL.
             * Devuelve false en el caso que no coincidan */
            bool ChequearSensores(const ListaDeSensores& sensores) const;
            bool ChequearActuadores(const ListaDeActuadores& actuadores) const;
            
            /* Escribe en el stream los ids
             * de los distintos elementos */
            void LoguearEncabezadoDeElementos(ostream& log_os);
            
            /* Escribe en el stream los estados
             * de cada uno de los elementos */
            void LoguearEstadoDeElementos(ostream& log_os);
            
            /* devuelve el elemento correspondiente al id
             * o NULL en el caso que no exista.
             * Sirve al resolver las entradas de cajas y actuadores
             * cuando se construyen en el parser */
            Elemento* ElementoPorId(const string& id_elemento);
            
            /* devuelve el comportamiento correspondiente al id
             * o NULL en el caso que no exista.
             * Sirve al resolver las transiciones
             * cuando se construyen en el parser */
            Comportamiento* ComportamientoPorId(const string& id_comportamiento);
            
        private:
            
            Comportamiento* comportamiento_actual;
            
            map<const string,Sensor*> _sensores;
            map<const string,Actuador*> _actuadores;
            map<const string,Timer*> _timers;
            map<const string,Contador*> _contadores;
            
            map<const string,Comportamiento*> _comportamientos;
            
            // funciones auxiliares
            
            void CambiarAComportamiento( const string& id );
            
            void ActualizarSensores(const EstadoDeSensores& estado_sensores);
            
    };

#endif // _ESTRUCTURAS_H_
