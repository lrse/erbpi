#include "parser.h"

XercesDOMParser *miParser;

/* funciones auxiliares */

bool inicializar_xerces()
{
  miParser = NULL; 
    try{
        XMLPlatformUtils::Initialize();
    }
    catch (const XMLException& toCatch){
        char* message = XMLString::transcode( toCatch.getMessage() );
        cout << "Error: Al inicializar el XML Parser: " << message << endl;
        XMLString::release( &message );
        return false;
    }
    return true;
}

int terminar_xerces()
{
  delete miParser;
    XMLPlatformUtils::Terminate();
    return 0;
}

XercesDOMParser* crear_y_configurar_parser()
{
    // creo el Parser XML
    XercesDOMParser *nuevoParser;
    nuevoParser = new XercesDOMParser;
    // configuro DOM Parser
    nuevoParser->setValidationScheme( XercesDOMParser::Val_Never );
    nuevoParser->setDoNamespaces( false );
    nuevoParser->setDoSchema( false );
    nuevoParser->setLoadExternalDTD( false );
    return nuevoParser;
}

// devuelve el valor del atributo de un tag
char* leer_atributo(DOMElement* xmlElemento, const char* atributo)
{
    XMLCh* atributoId = XMLString::transcode(atributo); // recasteo
    const XMLCh* xmlId = xmlElemento->getAttribute(atributoId); // obtengo el atributo
    return (XMLString::transcode(xmlId)); // recasteo y retorno el atributo
}

// llama a la funcion cargar_elemento
// para cada tag <nombre_elemento_hijo> hijo de xml_padre
template <class T>
void CargarHijosAObjeto
(
    T* objeto,
    DOMElement* xml_padre,
    const char* nombre_elemento_hijo,
    void (*cargar_elemento)(T*,DOMElement*,Conducta*),
    Conducta* conducta
)
{
    DOMNodeList* xml_lista_de_hijos = xml_padre->getElementsByTagName(XMLString::transcode(nombre_elemento_hijo));
    
    // Si tiene algun hijo
    if ( xml_lista_de_hijos )
    {
        // Obtengo la cantidad de hijos
        const  XMLSize_t cantidad_de_hijos = xml_lista_de_hijos->getLength();

        // Itero sobre los hijos
        for( XMLSize_t i = 0; i < cantidad_de_hijos; i++ )
        {
            // agarro el i-esimo nodo
            DOMNode* xml_nodo = xml_lista_de_hijos->item(i);
            
            // Si el nodo es un elemento (podria ser texto, y nidea que mas...)
            if( xml_nodo->getNodeType() && xml_nodo->getNodeType() == DOMNode::ELEMENT_NODE )
            {
                // lo casteo
                DOMElement* xml_hijo = dynamic_cast< xercesc::DOMElement* >( xml_nodo );
                cargar_elemento(objeto,xml_hijo,conducta);
            }
        }
    }
}

Comparacion_t Comparacion(const char* nombre)
{
    if (!strcmp(nombre,"="))
        return IGUAL;
    else if (!strcmp(nombre,"<"))
        return MENOR;
    else if (!strcmp(nombre,"<="))
        return MENOR_IGUAL;
    else if (!strcmp(nombre,">"))
        return MAYOR;
    else if (!strcmp(nombre,">="))
        return MAYOR_IGUAL;
    else
        cout << "OJO! comparacion desconocida '" << nombre << "'. Se devuelve IGUAL." << endl;
        
    return IGUAL;
}

Accion_t Accion(const char* nombre)
{
    if (!strcmp(nombre,"resetear"))
        return RESETEAR;
    else if (!strcmp(nombre,"incrementar"))
        return INCREMENTAR;
    else if (!strcmp(nombre,"decrementar"))
        return DECREMENTAR;
    else
        cout << "OJO! accion desconocida '" << nombre << "'. Se devuelve RESETEAR." << endl;
        
    return RESETEAR;
}

// =====================================================================
// funciones que cargan los distintos elementos

void CargarCondicion(Transicion* transicion, DOMElement* xml_condicion, Conducta* conducta)
{
    string id_elemento = string(leer_atributo(xml_condicion,"id_elemento"));
    Comparacion_t comparacion = Comparacion(leer_atributo(xml_condicion,"comparacion"));
    int umbral = atoi(leer_atributo(xml_condicion,"umbral"));
    
    Condicion* condicion = new Condicion(id_elemento,comparacion,umbral);
    transicion->AgregarCondicion(condicion);
}

void CargarActualizacionDeTimer(Transicion* transicion, DOMElement* xml_actualizacion, Conducta* conducta)
{
    string id_timer = leer_atributo(xml_actualizacion,"id_timer");
    Timer* timer = (Timer*) conducta->ElementoPorId(id_timer);
    
    if (timer)
        transicion->AgregarActualizacion(new ActualizacionDeTimer(timer));
    else
        cerr << "Error: no existe un timer con id " << id_timer << " para usar en actualizacion" << endl;
}

void CargarActualizacionDeContador(Transicion* transicion, DOMElement* xml_actualizacion, Conducta* conducta)
{
    string id_contador = leer_atributo(xml_actualizacion,"id_contador");
    Accion_t accion = Accion(leer_atributo(xml_actualizacion,"accion"));
    Contador* contador = (Contador*) conducta->ElementoPorId(id_contador);
        
    if (contador)
        transicion->AgregarActualizacion(new ActualizacionDeContador(contador,accion));
    else
        cerr << "Error: no existe un contador con id " << id_contador << " para usar en actualizador" << endl;
}

void CargarActualizacion(Transicion* transicion, DOMElement* xml_actualizacion, Conducta* conducta)
{
    const char* tipo = leer_atributo(xml_actualizacion,"tipo");
    
    if ( !strcmp(tipo,"timer") )
        CargarActualizacionDeTimer(transicion,xml_actualizacion,conducta);
    else if ( !strcmp(tipo,"contador") )
        CargarActualizacionDeContador(transicion,xml_actualizacion,conducta);
    else
        cout << "OJO! actualizacion con tipo desconocido '" << tipo << "'" << endl;
}

void CargarCondiciones(Transicion* transicion, DOMElement* xml_condiciones, Conducta* conducta)
{
    CargarHijosAObjeto<Transicion>(transicion,xml_condiciones,"condicion",&CargarCondicion,conducta);
}

void CargarActualizaciones(Transicion* transicion, DOMElement* xml_actualizaciones, Conducta* conducta)
{
    CargarHijosAObjeto<Transicion>(transicion,xml_actualizaciones,"actualizacion",&CargarActualizacion,conducta);
}

void CargarConeccion(Comportamiento* comportamiento, DOMElement* xml_coneccion, Conducta* conducta)
{
    string id_elemento_origen = string(leer_atributo(xml_coneccion,"src"));
    string id_elemento_destino = string(leer_atributo(xml_coneccion,"dst"));
    
    Elemento* elemento_origen = conducta->ElementoPorId(id_elemento_origen);
    ElementoConEntradas* elemento_destino = (ElementoConEntradas*) conducta->ElementoPorId(id_elemento_destino);
    
    cout << "   Cargando coneccion" << endl;
    
    if ( !elemento_origen  )
        cerr << "parser::CargarConeccion - Error: no existe un elemento con id " << id_elemento_origen << " para usar como entrada en el comportamiento" << endl;
    if ( !elemento_destino )
        cerr << "parser::CargarConeccion - Error: no existe un elemento con id " << id_elemento_destino << " para usar como destino en el comportamiento" << endl;
    else
        comportamiento->AgregarConeccion(new Coneccion(elemento_origen,elemento_destino));
}

void CargarConecciones(Comportamiento* comportamiento, DOMElement* xml_conecciones, Conducta* conducta)
{
    CargarHijosAObjeto(comportamiento,xml_conecciones,"coneccion",&CargarConeccion,conducta);
}

void CargarActuador(Conducta* conducta, DOMElement* xml_actuador, Conducta* _conducta)
{
    Actuador* actuador = new Actuador(leer_atributo(xml_actuador,"id"),leer_atributo(xml_actuador,"id"));
    
    conducta->AgregarActuador(actuador);
}

void CargarActuadores(Conducta* conducta, DOMElement* xml_actuadores, Conducta* _conducta)
{
    CargarHijosAObjeto(conducta,xml_actuadores,"actuador",&CargarActuador,conducta);
}

void CargarCaja(Comportamiento* comportamiento, DOMElement* xml_caja, Conducta* conducta)
{
    Punto punto_min;
    Punto punto_max;
    
    DOMNodeList* xml_hijos_punto = xml_caja->getElementsByTagName(XMLString::transcode("punto"));
    if ( xml_hijos_punto && xml_hijos_punto->getLength() > 1 )
    {
        DOMElement* xml_punto_min = dynamic_cast< xercesc::DOMElement* >( xml_hijos_punto->item(0) );
        DOMElement* xml_punto_max = dynamic_cast< xercesc::DOMElement* >( xml_hijos_punto->item(1) );
        
        punto_min.x = atoi( leer_atributo(xml_punto_min,"x") );
        punto_min.y = atoi( leer_atributo(xml_punto_min,"y") );
        
        punto_max.x = atoi( leer_atributo(xml_punto_max,"x") );
        punto_max.y = atoi( leer_atributo(xml_punto_max,"y") );
    }
    else
        cout << "Error: Hay una caja que no tiene suficientes puntos definidos" << endl;
    
    Caja* caja = new Caja(leer_atributo(xml_caja,"id"),leer_atributo(xml_caja,"id"),punto_min,punto_max);
    comportamiento->AgregarCaja(caja);
}

void CargarCajas(Comportamiento* comportamiento, DOMElement* xml_cajas, Conducta* conducta)
{
    CargarHijosAObjeto(comportamiento,xml_cajas,"caja",&CargarCaja,conducta);
}

void CargarSensor(Conducta* conducta, DOMElement* xml_sensor, Conducta* _conducta)
{
    Sensor* sensor = new Sensor(leer_atributo(xml_sensor,"id"),leer_atributo(xml_sensor,"id"));
    conducta->AgregarSensor(sensor);
}

void CargarSensores(Conducta* conducta, DOMElement* xml_sensores, Conducta* _conducta)
{
    CargarHijosAObjeto(conducta,xml_sensores,"sensor",&CargarSensor,conducta);
}

void CargarTransicion(Conducta* conducta, DOMElement* xml_transicion, Conducta* _conducta)
{
    // Todo: construyendo string de char*. asegurarse que esto funcione
    string id = string(leer_atributo(xml_transicion,"id"));
    string id_comportamiento_origen = string(leer_atributo(xml_transicion,"id_origen"));
    string id_comportamiento_destino = string(leer_atributo(xml_transicion,"id_destino"));
    
    Transicion* transicion = new Transicion(id,id_comportamiento_destino);
    
    CargarHijosAObjeto<Transicion>(transicion,xml_transicion,"actualizaciones",&CargarActualizaciones,conducta);
    CargarHijosAObjeto<Transicion>(transicion,xml_transicion,"condiciones",&CargarCondiciones,conducta);
    
    Comportamiento* comportamiento_origen = conducta->ComportamientoPorId(id_comportamiento_origen);
    
    if (comportamiento_origen)
        comportamiento_origen->AgregarTransicion(transicion);
    else
        cout << "Error: Se trato de agregar una transicion a un comportamiento origen inexistente id: " << id_comportamiento_origen << endl;
}

void CargarTransiciones(Conducta* conducta, DOMElement* xml_lista_transicion, Conducta* _conducta)
{
    CargarHijosAObjeto(conducta,xml_lista_transicion,"transicion",&CargarTransicion,conducta);
}

void CargarTimer(Conducta* conducta, DOMElement* xml_timer, Conducta* _conducta)
{
    Timer* timer = new Timer(leer_atributo(xml_timer,"id"),leer_atributo(xml_timer,"descripcion"));
    conducta->AgregarTimer(timer);
}

void CargarTimers(Conducta* conducta, DOMElement* xml_timers, Conducta* _conducta)
{
    CargarHijosAObjeto(conducta,xml_timers,"timer",&CargarTimer,conducta);
}

void CargarContador(Conducta* conducta, DOMElement* xml_contador, Conducta* _conducta)
{
    Contador* contador = new Contador(leer_atributo(xml_contador,"id"),leer_atributo(xml_contador,"descripcion"));
    conducta->AgregarContador(contador);
}

void CargarContadores(Conducta* conducta, DOMElement* xml_contadores, Conducta* _conducta)
{
    CargarHijosAObjeto(conducta,xml_contadores,"contador",&CargarContador,conducta);
}

void CargarComportamiento(Conducta* conducta, DOMElement* xml_comportamiento, Conducta* _conducta)
{
    Comportamiento* comportamiento = new Comportamiento(leer_atributo(xml_comportamiento,"id"),leer_atributo(xml_comportamiento,"descripcion"));
    conducta->AgregarComportamiento(comportamiento);
    
    cout << "Cargando comportamiento " << comportamiento->GetDescripcion() << endl;
    
    // cargar funciones
    CargarHijosAObjeto(comportamiento,xml_comportamiento,"cajas",&CargarCajas,conducta);
    
    // cargar conecciones (requiere sensores/actuadores/funciones cargadas)
    CargarHijosAObjeto(comportamiento,xml_comportamiento,"conecciones",&CargarConecciones,conducta);
}

// =====================================================================

void init_parser(void) { 
  inicializar_xerces(); 
    miParser = crear_y_configurar_parser(); 
} 
     
void deinit_parser(void) { 
  // terminar la infraestructura del Xerces 
    terminar_xerces();   
}

int parsear(string xml_string, Conducta* conducta)
{
    try
    {
        //cout << "parsing: " << xml_string << endl; 
        MemBufInputSource src((const XMLByte*)xml_string.c_str(), xml_string.length(), "dummy", false); 
        cout << "tengo buffer de " << xml_string.length() << " bytes" << endl; 
        miParser->parse(src);
  
        // Obtener el "DOCUMENTO" XML // no es necesario liberar este puntero...
        cout << "parseado, get document" << endl; 
        DOMDocument* xmlDoc = miParser->getDocument();
        
        // Obtener "Elemento RAIZ" del XML
        DOMElement* xml_conducta = xmlDoc->getDocumentElement();
        
        // chequear que sea <conducta>
        if( !(XMLString::equals(xml_conducta->getTagName(), XMLString::transcode("conducta"))) )
        {
            cout << "Error: La especificación del XML no corresponde !!!" << endl;
            return 1;
            // NOTA: el XML puede contener otras cosas, pero debe comenzar con "<conducta></conducta>"  
        }
        
        // chequear que <conducta> no esté vacío...
        if( !xml_conducta )
        {
            cout << "Error: El archivo XML está vacío !!!" << endl;
            return 1;
        }
        
        cout << "paso tests, empiezo a leer" << endl; 
        
        // cargar sensores
        CargarHijosAObjeto(conducta,xml_conducta,"sensores",&CargarSensores,conducta);
        
        // cargar actuadores
        CargarHijosAObjeto(conducta,xml_conducta,"actuadores",&CargarActuadores,conducta);
        
        // cargar timers
        CargarHijosAObjeto(conducta,xml_conducta,"timers",&CargarTimers,conducta);
        
        // cargar contadores
        CargarHijosAObjeto(conducta,xml_conducta,"contadores",&CargarContadores,conducta);
        
        // cargar comportamientos (requiere sensores y actuadores cargados)
        CargarHijosAObjeto(conducta,xml_conducta,"comportamiento",&CargarComportamiento,conducta);
        
        // cargar transiciones (requiere contadores/timers/comportamientos cargados)
        CargarHijosAObjeto(conducta,xml_conducta,"transiciones",&CargarTransiciones,conducta);
        
        const char* inicial = leer_atributo(xml_conducta,"id_comportamiento_inicial");
        conducta->SetComportamientoInicial(inicial);
    }
    catch (const XMLException& toCatch)
    {
        char* message = XMLString::transcode( toCatch.getMessage() );
        cout << "Error: algún error parseando el archivo: " << message << endl;
        XMLString::release( &message );
        return 1;
    }

    return 0;
}

