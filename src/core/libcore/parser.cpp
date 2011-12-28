#include "parser.h"

XercesDOMParser *miParser; 
/* estructuras auxiliares */

typedef pair<ElementoConEntradas*,string> EntradaAuxiliar;

typedef struct
{
	Transicion* transicion;
	string id_timer;
} ActualizacionTimerAuxiliar;

typedef struct
{
	Transicion* transicion;
	string id_contador;
	Accion_t accion;
} ActualizacionContadorAuxiliar;

/* variable global auxiliar */

set<EntradaAuxiliar> entradas_auxiliares;
set<ActualizacionTimerAuxiliar*> actualizaciones_timer_auxiliares;
set<ActualizacionContadorAuxiliar*> actualizaciones_contador_auxiliares;

/* funciones auxiliares */

/* int ordenar_topologicamente(vector<TuplaAuxiliar>& vectorAux)
 * 
 * TODO
 * 
{
	// el orden totoplogico lo devuelve por "vectorAux"
	// esta función detecta ciclos y devuelve error si los hay...
	// Ojo con abusar de esta función, creo que pertenece a O(n^3)...
	// esta función asume que no hay elementos repetidos por IDs...
	int flag, i, j, k, gradoEntradaCero;
	int n = vectorAux.size(); // calculo el tamaño
	vector<TuplaAuxiliar> vectorAux2; // será una copia "ordenada topologicamente" de vectorAux
	int matrizAdyacenciaAux[n][n]; // creo la matrizAdyacenciaAux para el sorting
	int incluidos[n]; // vector auxiliar para el sorting, son lo que ya fueron ordenados...

	// inicializo "incluidos" todo en "false" y la matriz de adyacencia toda en "-1"
	for (i = 0; i < n; i++){
		incluidos[i] = false;
		for (j = 0; j < n; j++)
			matrizAdyacenciaAux[i][j] = -1;
	}
	
	// configuro la "matrizAdyacenciaAux", si (matrizAdyacenciaAux[i][j] == 1) => (i-->j), o sea, "i" es antecesor de "j".
	// o sea, hay que recorrerla por columnas para que ande bien...
	for (i = 0; i < n; i++)
		for (j = 0; j < ((vectorAux[i]).entradas).size(); j++)
			for (k = 0; k < n; k++)
				if ( vectorAux[i].entradas[j] == vectorAux[k].id ){
					matrizAdyacenciaAux[k][i] = 1;
					break;
				}
	
	// ahora empiezo el sorting...
	vectorAux2.clear(); // limpio el vectorAux2 antes de empezar el soroting...
	flag = true;
	while ( flag ){
		for (i = 0; i < n; i++){ // recorro por columnas
			if ( !incluidos[i] ){ // el "i" todavía no está incluido
				gradoEntradaCero = true;
				for (j = 0; j < n; j++){ // busco si "i" tiene predecesores
					if ( matrizAdyacenciaAux[j][i] > 0 ){ // el "i" todavía tiene predecesores
						gradoEntradaCero = false;
						break;
					}
				}
				if ( gradoEntradaCero ){ // "i" no tiene predecesores => lo agrego al resultado
					incluidos[i] = true; // incluyo a "i"
					vectorAux2.push_back( vectorAux[i] ); // pongo el nuevo item en el vectorAux2 al final
					for (j = 0; j < n; j++){ // corrijo la matriz de adyacencia porque "saqué" a "i", todos los que tenían a "i" como predecesor ya no lo tienen...
						matrizAdyacenciaAux[i][j] = -1;
						matrizAdyacenciaAux[j][i] = -1;
					}
					break;
				}
			}
		}
		if ( i == n ){
			if ( !gradoEntradaCero )
				return 1; // OJO: EL GRAFO TIENE CICLOS !!!  => retornar ERROR !!
			else flag = false;
		}
	}
	
	// ordeno vectorAux para devolver con el orden del sorting en vectorAux2
	vectorAux.clear();
	for (i = 0; i < n; i++)
		vectorAux.push_back( vectorAux2[i] );
	return 0;
}
*/

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
void CargarElementosDeListaAObjeto
(
	T* objeto,
	DOMElement* xml_padre,
	const char* nombre_elemento_hijo,
	void (*cargar_elemento)(T*,DOMElement*)
)
{
	// obtengo la lista de hijos de actuadores
	DOMNodeList* xml_lista_de_hijos = xml_padre->getChildNodes();
	
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
				
				// me fijo si el tag es nombre_elemento
				if ( XMLString::equals(xml_hijo->getTagName(), XMLString::transcode(nombre_elemento_hijo)))
					cargar_elemento(objeto,xml_hijo);
				else
					cout << "hay un tag <" << XMLString::transcode(xml_hijo->getTagName()) << "> donde solo se esperaban tags <" << nombre_elemento_hijo << ">" << endl;
			}
		}
	}
}

Comparacion_t Comparacion(const char* nombre)
{
	if (!strcmp(nombre,"igual"))
		return IGUAL;
	else if (!strcmp(nombre,"menor"))
		return MENOR;
	else if (!strcmp(nombre,"menor_igual"))
		return MENOR_IGUAL;
	else if (!strcmp(nombre,"mayor"))
		return MAYOR;
	else if (!strcmp(nombre,"mayor_igual"))
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

void CargarCondicion(Transicion* transicion, DOMElement* xml_condicion)
{
	string id_elemento = string(leer_atributo(xml_condicion,"id_elemento"));
	Comparacion_t comparacion = Comparacion(leer_atributo(xml_condicion,"comparacion"));
	int umbral = atoi(leer_atributo(xml_condicion,"umbral"));
	
	Condicion* condicion = new Condicion(id_elemento,comparacion,umbral);
	transicion->AgregarCondicion(condicion);
}

void CargarActualizacion(Transicion* transicion, DOMElement* xml_actualizacion)
{
	const char* tipo = leer_atributo(xml_actualizacion,"tipo");
	
	if ( !strcmp(tipo,"timer") )
	{
		ActualizacionTimerAuxiliar* actualizacion = new ActualizacionTimerAuxiliar();
		actualizacion->transicion = transicion;
		actualizacion->id_timer = leer_atributo(xml_actualizacion,"id_timer");
		
		actualizaciones_timer_auxiliares.insert( actualizacion );
		
	}
	else if ( !strcmp(tipo,"contador") )
	{
		ActualizacionContadorAuxiliar* actualizacion = new ActualizacionContadorAuxiliar();
		actualizacion->transicion = transicion;
		actualizacion->id_contador = leer_atributo(xml_actualizacion,"id_contador");;
		actualizacion->accion = Accion(leer_atributo(xml_actualizacion,"accion"));
		
		actualizaciones_contador_auxiliares.insert( actualizacion );
	}
	else
		cout << "OJO! actualizacion con tipo desconocido '" << tipo << "'" << endl;
}

void CargarCondiciones(Transicion* transicion, DOMElement* xml_condiciones)
{
	CargarElementosDeListaAObjeto<Transicion>(transicion,xml_condiciones,"condicion",&CargarCondicion);
}

void CargarActualizaciones(Transicion* transicion, DOMElement* xml_actualizaciones)
{
	CargarElementosDeListaAObjeto<Transicion>(transicion,xml_actualizaciones,"actualizacion",&CargarActualizacion);
}

void CargarEntrada(ElementoConEntradas* elemento, DOMElement* xml_entrada)
{
	EntradaAuxiliar entrada(elemento,leer_atributo(xml_entrada,"id_entrada"));
	entradas_auxiliares.insert( entrada );
}

void CargarEntradas(ElementoConEntradas* elemento, DOMElement* xml_entradas)
{
	CargarElementosDeListaAObjeto<ElementoConEntradas>(elemento,xml_entradas,"entrada",&CargarEntrada);
}

void CargarActuador(Comportamiento* comportamiento, DOMElement* xml_actuador)
{
	Actuador* actuador = new Actuador(leer_atributo(xml_actuador,"id"));
	comportamiento->AgregarActuador(actuador);
	
	CargarElementosDeListaAObjeto<ElementoConEntradas>(actuador,xml_actuador,"entradas",&CargarEntradas);
}

void CargarCaja(Comportamiento* comportamiento, DOMElement* xml_caja)
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
	
	Caja* caja = new Caja(leer_atributo(xml_caja,"id"),punto_min,punto_max);
	comportamiento->AgregarCaja(caja);
	
	DOMNodeList* xml_lista_entradas = xml_caja->getElementsByTagName(XMLString::transcode("entradas"));
	if ( xml_lista_entradas && xml_lista_entradas->getLength() > 0 )
	{
		DOMElement* xml_entradas = dynamic_cast< xercesc::DOMElement* >( xml_lista_entradas->item(0) );
		CargarElementosDeListaAObjeto<ElementoConEntradas>(caja,xml_entradas,"entrada",&CargarEntrada);
	}
}

void CargarSensor(Conducta* conducta, DOMElement* xml_sensor)
{
	Sensor* sensor = new Sensor(leer_atributo(xml_sensor,"id"));
	conducta->AgregarSensor(sensor);
}

void CargarTransicion(Comportamiento* comportamiento, DOMElement* xml_transicion)
{
	// Todo: construyendo string de char*. asegurarse que esto funcione
	string id = string(leer_atributo(xml_transicion,"id"));
	string id_comportamiento_destino = string(leer_atributo(xml_transicion,"id_destino"));
	
	Transicion* transicion = new Transicion(id,id_comportamiento_destino);
	comportamiento->AgregarTransicion(transicion);
	
	/* TODO: cargar y hacer andar las actualizaciones */
	
	DOMNodeList* xml_lista_condiciones = xml_transicion->getElementsByTagName(XMLString::transcode("condiciones"));
	if ( xml_lista_condiciones && xml_lista_condiciones->getLength() > 0 )
	{
		DOMElement* xml_condiciones = dynamic_cast< xercesc::DOMElement* >( xml_lista_condiciones->item(0) );
		CargarElementosDeListaAObjeto<Transicion>(transicion,xml_condiciones,"condicion",&CargarCondicion);
	}
	
}

void CargarTimer(Conducta* conducta, DOMElement* xml_timer)
{
	Timer* timer = new Timer(leer_atributo(xml_timer,"id"));
	conducta->AgregarTimer(timer);
}

void CargarContador(Conducta* conducta, DOMElement* xml_contador)
{
	Contador* contador = new Contador(leer_atributo(xml_contador,"id"));
	conducta->AgregarContador(contador);
}

void CargarComportamiento(Conducta* conducta, DOMElement* xml_comportamiento)
{
	Comportamiento* comportamiento = new Comportamiento(leer_atributo(xml_comportamiento,"id"));
	conducta->AgregarComportamiento(comportamiento);
	
	// obtengo la lista de hijos de actuadores
	DOMNodeList* xml_lista_hijos_comportamiento = xml_comportamiento->getChildNodes();
	
	// Si tiene algun hijo
	if ( xml_lista_hijos_comportamiento )
	{
		// Obtengo la cantidad de hijos de <comportamiento>
		const  XMLSize_t cantidad_xml_hijos_comportamiento = xml_lista_hijos_comportamiento->getLength();

		// Itero sobre los hijos de <timers>
		for( XMLSize_t i = 0; i < cantidad_xml_hijos_comportamiento; i++ )
		{
			// agarro el i-esimo nodo
			DOMNode* xml_nodo = xml_lista_hijos_comportamiento->item(i);
			
			// Si el nodo es un elemento (podria ser texto, y nidea que mas...)
			if( xml_nodo->getNodeType() && xml_nodo->getNodeType() == DOMNode::ELEMENT_NODE )
			{
				// lo casteo
				DOMElement* xml_hijo_de_comportamiento = dynamic_cast< xercesc::DOMElement* >( xml_nodo );
				
				// me fijo si el tag es <actuadores>
				if ( XMLString::equals(xml_hijo_de_comportamiento->getTagName(), XMLString::transcode("actuadores")))
					CargarElementosDeListaAObjeto<Comportamiento>(comportamiento,xml_hijo_de_comportamiento,"actuador",&CargarActuador);
				
				// me fijo si el tag es <cajas>
				else if ( XMLString::equals(xml_hijo_de_comportamiento->getTagName(), XMLString::transcode("cajas")))
					CargarElementosDeListaAObjeto<Comportamiento>(comportamiento,xml_hijo_de_comportamiento,"caja",&CargarCaja);
				
				// me fijo si el tag es <transiciones>
				else if ( XMLString::equals(xml_hijo_de_comportamiento->getTagName(), XMLString::transcode("transiciones")))
					CargarElementosDeListaAObjeto<Comportamiento>(comportamiento,xml_hijo_de_comportamiento,"transicion",&CargarTransicion);
				
				else
				{
					cout << "hay un tag no esperado <" << XMLString::transcode(xml_hijo_de_comportamiento->getTagName()) << "> como hijo de un comportamiento" << endl;
				}
			}
		}
	}
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
	// limpio los vectores auxiliares
	
	entradas_auxiliares.clear();
	actualizaciones_timer_auxiliares.clear();
	actualizaciones_contador_auxiliares.clear();

	try{
		cout << "parsing: " << xml_string << endl; 
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

		// Obtengo los "Nodos HIJOS de la Raiz"
		DOMNodeList* xml_hijos_conducta = xml_conducta->getChildNodes();
		// chequeo que no esté vacío
		if( !xml_hijos_conducta )
		{
			cout << "Error: El archivo XML está vacío !!!" << endl;
			return 1;
		}
		
		// Obtengo la "CANTIDAD de Hijos" // debería ser CANTIDAD == 3 (sensores, cajas y actuadores)
		const  XMLSize_t cantidad_xml_hijos_conducta = xml_hijos_conducta->getLength();

		// Itero sobre los hijos de <conducta> y cargo cada uno
		for( XMLSize_t i = 0; i < cantidad_xml_hijos_conducta; i++ )
		{
			// agarro el i-esimo nodo
			DOMNode* xml_nodo = xml_hijos_conducta->item(i);
			
			// Si el nodo es un elemento (podria ser texto, y nidea que mas...)
			if( xml_nodo->getNodeType() && xml_nodo->getNodeType() == DOMNode::ELEMENT_NODE )
			{
				// lo casteo
				DOMElement* xml_hijo_de_conducta = dynamic_cast< xercesc::DOMElement* >( xml_nodo );
				
				// me fijo si el tag es <sensores>
				if ( XMLString::equals(xml_hijo_de_conducta->getTagName(), XMLString::transcode("sensores")))
					CargarElementosDeListaAObjeto<Conducta>(conducta,xml_hijo_de_conducta,"sensor",&CargarSensor);
				
				// me fijo si el tag es <timers>
				else if ( XMLString::equals(xml_hijo_de_conducta->getTagName(), XMLString::transcode("timers")))
					CargarElementosDeListaAObjeto<Conducta>(conducta,xml_hijo_de_conducta,"timer",&CargarTimer);
				
				// me fijo si el tag es <contadores>
				else if ( XMLString::equals(xml_hijo_de_conducta->getTagName(), XMLString::transcode("contadores")))
					CargarElementosDeListaAObjeto<Conducta>(conducta,xml_hijo_de_conducta,"contador",&CargarContador);
				
				// me fijo si es un <comportamiento>
				else if ( XMLString::equals(xml_hijo_de_conducta->getTagName(), XMLString::transcode("comportamiento")))
					CargarComportamiento(conducta,xml_hijo_de_conducta);
				
				else
					cout << "hay un tag no esperado <" << XMLString::transcode(xml_hijo_de_conducta->getTagName()) << "> como hijo de una conducta" << endl;
			}
		}
		
		const char* inicial = leer_atributo(xml_conducta,"id_comportamiento_inicial");
		conducta->SetComportamientoInicial(inicial);
		
		// Ya estan cargados todos los elementos
		// asi que puedo definir las entradas
		forall(it,entradas_auxiliares)
		{
			Elemento* entrada = conducta->ElementoPorId(it->second);
			
			// corroboro que exista una entrada con ese id
			if (!entrada)
			{
				cout << "Error: no existe un elemento con id " << it->second << " para usar como entrada" << endl;
				return 1;
			}
			
			(it->first)->AgregarEntrada(entrada);
		}
		
		forall(it,actualizaciones_timer_auxiliares)
		{
			
			Timer* timer = (Timer*) (conducta->ElementoPorId((*it)->id_timer));
			
			// corroboro que exista una timer con ese id
			if (!timer)
			{
				cout << "Error: no existe un timer con id " << (*it)->id_timer << " para usar en actualizador" << endl;
				return 1;
			}
			
			(*it)->transicion->AgregarActualizacion(new ActualizacionDeTimer(timer));
		}
		
		forall(it,actualizaciones_contador_auxiliares)
		{
			
			Contador* contador = (Contador*) (conducta->ElementoPorId((*it)->id_contador));
			
			// corroboro que exista un contador con ese id
			if (!contador)
			{
				cout << "Error: no existe un contador con id " << (*it)->id_contador << " para usar en actualizador" << endl;
				return 1;
			}
			
			(*it)->transicion->AgregarActualizacion(new ActualizacionDeContador(contador,(*it)->accion));
		}
		
	} // end try
	
	catch (const XMLException& toCatch){
		char* message = XMLString::transcode( toCatch.getMessage() );
		cout << "Error: algún error parseando el archivo: " << message << endl;
		XMLString::release( &message );
		return 1;
	}

// 2. CHEQUEAR QUE NO HAYA IDs REPETIDOS

// 4. TOPOLOGICAL SORTING CON CHEQUEO DE CICLOS
/*
 * TODO
 * 
	if ( ordenar_topologicamente(vectorAuxiliar) ){
		cout << "Error: el grafo tiene ciclos !!!" << endl;
		return 1;
	}
*/
	return 0;
}

