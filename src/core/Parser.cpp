#include "Parser.h"

// ESTRUCTURA AUXILIAR PARA EL PARSER	
struct TuplaAuxiliar {
  int tipo;
  string id;
  vector<string> entradas;
  vector<Punto> puntos;
};

int inicializar_xerces(){
	try{
		XMLPlatformUtils::Initialize();
	}
	catch (const XMLException& toCatch){
		char* message = XMLString::transcode( toCatch.getMessage() );
		cout << "Error: Al inicializar el XML Parser: " << message << endl;
		XMLString::release( &message );
		return 1;
	}
	return 0;
}

int terminar_xerces(){
	XMLPlatformUtils::Terminate();
	return 0;
}

XercesDOMParser* crear_y_configurar_parser(){
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

char* leer_id(DOMElement* xmlElemento){
	XMLCh* atributoId = XMLString::transcode(NOMBRE_ATRIBUTO_ID); // recasteo
	const XMLCh* xmlId = xmlElemento->getAttribute(atributoId); // obtengo el atributo ID y recasteo
	return (XMLString::transcode(xmlId)); // recasteo y retorno el ID
}

int leer_entradas(DOMElement* xmlElemento, vector<string>& vectorAux){
	DOMNodeList* xmlElementoHijos = xmlElemento->getChildNodes(); // Obtengo la "LISTA de Hijos del Elemento"
	const  XMLSize_t xmlElementoHijosCantidad = xmlElementoHijos->getLength(); // Obtengo la "CANTIDAD de Entradas"
	for( XMLSize_t i = 0; i < xmlElementoHijosCantidad; i++ ){
		DOMNode* xmlNodo = xmlElementoHijos->item(i); // obtengo el HIJO(i)
		if( xmlNodo->getNodeType() && xmlNodo->getNodeType() == DOMNode::ELEMENT_NODE ){ // El Nodo es un elemento => lo re-casteo como un elemento // getNodeType() = TRUE => (es != NULL) // getNodeType() = DOMNode::ELEMENT_NODE => (es un elemento)
			DOMElement* xmlElemento2 = dynamic_cast< xercesc::DOMElement* >( xmlNodo );
			if( XMLString::equals(xmlElemento2->getTagName(), XMLString::transcode(NOMBRE_ENTRADAS))){ // obtengo el ¿NOMBRE=ENTRADAS? del elemento y recasteo
				DOMNodeList* xmlEntradas = xmlElemento2->getChildNodes(); // Obtengo la "LISTA de Entradas"
				const  XMLSize_t xmlEntradasCantidad = xmlEntradas->getLength(); // Obtengo la "CANTIDAD de Entradas"
				for( XMLSize_t j = 0; j < xmlEntradasCantidad; j++ ){ // Lleno el vector de entradas
					DOMNode* xmlNodoActual2 = xmlEntradas->item(j); // obtengo el HIJO(j)
					if( xmlNodoActual2->getNodeType() && xmlNodoActual2->getNodeType() == DOMNode::ELEMENT_NODE ){ // El Nodo es un elemento => lo re-casteo como un elemento // getNodeType() = TRUE => (es != NULL) // getNodeType() = DOMNode::ELEMENT_NODE => (es un elemento)
						DOMElement* xmlElementoActual2 = dynamic_cast< xercesc::DOMElement* >( xmlNodoActual2 );
						if( XMLString::equals(xmlElementoActual2->getTagName(), XMLString::transcode(NOMBRE_ENTRADA))) // obtengo el ¿NOMBRE=<entrada>? => lo agrego, si no, no!
							vectorAux.push_back(leer_id(xmlElementoActual2)); // Obtengo el ID y lo agrego al vector resultado
					}
				}
			}
		}
	}
	return 0;
}

int leer_puntos(DOMElement* xmlElemento, vector<Punto>& vectorAux){
	XMLCh* atributoX = XMLString::transcode(NOMBRE_ATRIBUTO_PUNTO_X); // recasteo
	XMLCh* atributoY = XMLString::transcode(NOMBRE_ATRIBUTO_PUNTO_Y); // recasteo
	DOMNodeList* xmlElementoHijos = xmlElemento->getChildNodes(); // Obtengo la "LISTA de Hijos del Elemento"
	const  XMLSize_t xmlElementoHijosCantidad = xmlElementoHijos->getLength(); // Obtengo la "CANTIDAD de Entradas"
	for( XMLSize_t i = 0; i < xmlElementoHijosCantidad; i++ ){
		DOMNode* xmlNodo = xmlElementoHijos->item(i); // obtengo el HIJO(i)
		if( xmlNodo->getNodeType() && xmlNodo->getNodeType() == DOMNode::ELEMENT_NODE ){ // El Nodo es un elemento => lo re-casteo como un elemento // getNodeType() = TRUE => (es != NULL) // getNodeType() = DOMNode::ELEMENT_NODE => (es un elemento)
			DOMElement* xmlElemento2 = dynamic_cast< xercesc::DOMElement* >( xmlNodo );
			if( XMLString::equals(xmlElemento2->getTagName(), XMLString::transcode(NOMBRE_PUNTOS))){ // obtengo el ¿NOMBRE=PUNTOS? del elemento y recasteo
				DOMNodeList* xmlPuntos = xmlElemento2->getChildNodes(); // Obtengo la "LISTA de Puntos"
				const  XMLSize_t xmlPuntosCantidad = xmlPuntos->getLength(); // Obtengo la "CANTIDAD de Puntos"
				for( XMLSize_t j = 0; j < xmlPuntosCantidad; j++ ){ // Lleno el vector de puntos
					DOMNode* xmlNodoActual2 = xmlPuntos->item(j); // obtengo el HIJO(j)
					if( xmlNodoActual2->getNodeType() && xmlNodoActual2->getNodeType() == DOMNode::ELEMENT_NODE ){ // El Nodo es un elemento => lo re-casteo como un elemento // getNodeType() = TRUE => (es != NULL) // getNodeType() = DOMNode::ELEMENT_NODE => (es un elemento)
						DOMElement* xmlElementoActual2 = dynamic_cast< xercesc::DOMElement* >( xmlNodoActual2 );
						if( XMLString::equals(xmlElementoActual2->getTagName(), XMLString::transcode(NOMBRE_PUNTO))){ // obtengo el ¿NOMBRE=<punto>? => lo agrego, si no, no!
							const XMLCh* xmlX = xmlElementoActual2->getAttribute(atributoX); // obtengo el atributo X y recasteo
							const XMLCh* xmlY = xmlElementoActual2->getAttribute(atributoY); // obtengo el atributo Y y recasteo
							Punto nuevoPunto; // creo el nuevo punto
							nuevoPunto.x = atoi((XMLString::transcode(xmlX))); // seteo X del punto y recasteo
							nuevoPunto.y = atoi((XMLString::transcode(xmlY))); // seteo Y del punto y recasteo
							vectorAux.push_back(nuevoPunto); // agrego el punto al vector resultado
						}
					}
				}
			}
		}
	}
	return 0;
}

int leer_sensores(DOMNodeList* listaNodos, XMLSize_t cantidadNodos, vector<TuplaAuxiliar>& vectorAux){
	TuplaAuxiliar tupla;
	for( XMLSize_t i = 0; i < cantidadNodos; i++ ){
		DOMNode* xmlNodoActual = listaNodos->item(i); // obtengo el HIJO(i)
		if( xmlNodoActual->getNodeType() && xmlNodoActual->getNodeType() == DOMNode::ELEMENT_NODE ){ // El Nodo es un elemento => lo re-casteo como un elemento // getNodeType() = TRUE => (es != NULL) // getNodeType() = DOMNode::ELEMENT_NODE => (es un elemento)
			DOMElement* xmlElementoActual = dynamic_cast< xercesc::DOMElement* >( xmlNodoActual ); 
			if( XMLString::equals(xmlElementoActual->getTagName(), XMLString::transcode(NOMBRE_SENSOR))){ // obtengo el ¿NOMBRE=<sensor>? => lo agrego, si no, no!
				// seteo tipo y id del sensor
				tupla.tipo = TIPO_SENSOR;
				tupla.id = leer_id(xmlElementoActual);
				vectorAux.push_back(tupla); // agrego el sensor al vector
			}
		}
	}
	return 0;
}

int leer_cajas(DOMNodeList* listaNodos, XMLSize_t cantidadNodos, vector<TuplaAuxiliar>& vectorAux){
	TuplaAuxiliar tupla;
	vector<string> vectorEntradasId;
	vector<Punto> vectorPuntosXY;
	for( XMLSize_t i = 0; i < cantidadNodos; i++ ){
		DOMNode* xmlNodoActual = listaNodos->item(i); // obtengo el HIJO(i)
		if( xmlNodoActual->getNodeType() && xmlNodoActual->getNodeType() == DOMNode::ELEMENT_NODE ){ // El Nodo es un elemento => lo re-casteo como un elemento // getNodeType() = TRUE => (es != NULL) // getNodeType() = DOMNode::ELEMENT_NODE => (es un elemento)
			DOMElement* xmlElementoActual = dynamic_cast< xercesc::DOMElement* >( xmlNodoActual );
			if( XMLString::equals(xmlElementoActual->getTagName(), XMLString::transcode(NOMBRE_CAJA))){ // obtengo el ¿NOMBRE=<caja>? => lo agrego, si no, no!
				vectorEntradasId.clear(); // limpio el vector
				vectorPuntosXY.clear(); // limpio el vector
				leer_entradas(xmlElementoActual, vectorEntradasId); // obtengo el vector con los IDs de las entradas de la caja
				leer_puntos(xmlElementoActual, vectorPuntosXY); // obtengo el vector con los XYs de los puntos de la caja
				if ( vectorPuntosXY.size() != 2 ){
					cout << "Error: las cajas deben tener exactamente 2 puntos definidos !!!" << endl;
					return 1;
				}
				// seteo tipo, id, entradas y puntos de la caja
				tupla.tipo = TIPO_CAJA;
				tupla.id = leer_id(xmlElementoActual);
				tupla.entradas = vectorEntradasId;
				tupla.puntos = vectorPuntosXY;
				vectorAux.push_back(tupla); // agrego el sensor al vector
			}
		}
	}
	return 0;
}

int leer_actuadores(DOMNodeList* listaNodos, XMLSize_t cantidadNodos, vector<TuplaAuxiliar>& vectorAux){
	TuplaAuxiliar tupla;
	vector<string> vectorEntradasId;
	for( XMLSize_t i = 0; i < cantidadNodos; i++ ){
		DOMNode* xmlNodoActual = listaNodos->item(i); // obtengo el HIJO(i)
		if( xmlNodoActual->getNodeType() && xmlNodoActual->getNodeType() == DOMNode::ELEMENT_NODE ){ // El Nodo es un elemento => lo re-casteo como un elemento // getNodeType() = TRUE => (es != NULL) // getNodeType() = DOMNode::ELEMENT_NODE => (es un elemento)
			DOMElement* xmlElementoActual = dynamic_cast< xercesc::DOMElement* >( xmlNodoActual );
			if( XMLString::equals(xmlElementoActual->getTagName(), XMLString::transcode(NOMBRE_ACTUADOR))){ // obtengo el ¿NOMBRE=<caja>? => lo agrego, si no, no!
				vectorEntradasId.clear(); // limpio el vector
				leer_entradas(xmlElementoActual, vectorEntradasId); // obtengo el vector con los IDs de las entradas del actuador
				// seteo tipo, id y entradas del actuador
				tupla.tipo = TIPO_ACTUADOR;
				tupla.id = leer_id(xmlElementoActual);
				tupla.entradas = vectorEntradasId;
				vectorAux.push_back(tupla); // agrego el actuador al vector
			}
		}
	}
	return 0;
}

int ordenar_topologicamente(vector<TuplaAuxiliar>& vectorAux){
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

int parsear(string XMLArchivo, vector<Elemento*>& tabla_ejecucion){
	int i, j, k;
	vector<TuplaAuxiliar> vectorAuxiliar; // vectorAuxiliar para el parseo inicial
	Sensor* nuevoSensor; // sensor para agregar a tabla_ejecucion
	Caja* nuevaCaja; // caja para agregar a tabla_ejecucion
	Actuador* nuevoActuador; // actuador para agregar a tabla_ejecucion
	
// 1. GENERO EL "VECTORAUXILIAR" CON LOS ELEMENTOS PARSEADOS DEL XML
	
	// inicio la infraestructura del Xerces
	if ( inicializar_xerces() ){
		cout << "Error: al inicializar las librerías del Xerces perser !!!" << endl;
		return 1;
	}

	// creo y configuro el Parser XML...
	XercesDOMParser *miParser;
	miParser = crear_y_configurar_parser();

	// chequeo que el archivo a parsear esté OK...
	struct stat estadoArchivo;
	int estado = stat(XMLArchivo.c_str(), &estadoArchivo);
	if( estado == ENOENT ) cout << "Error: La ruta del archivo no existe o no está el archivo" << endl;
	else if( estado == ENOTDIR ) cout << "Error: Algo en la ruta del archivo no es un directorio" << endl;
	else if( estado == ELOOP ) cout << "Error: Demasiados `links' encontrados en la ruta del archivo" << endl;
	else if( estado == EACCES ) cout << "Error: No tiene permisos para el archivo" << endl;
	else if( estado == ENAMETOOLONG ) cout << "Error: No se puede leer el archivo" << endl;
	else if( estado == -1 ) cout << "Error: No se puede leer el archivo" << endl;
	if ((estado == -1) || (estado == ENOENT) || (estado == ENOTDIR) || (estado == ELOOP) || (estado == EACCES) || (estado == ENAMETOOLONG)) return 1;

	try{
		// Inicio el parseo... // hay que meterle ".c_str()" si no no anda...
		miParser->parse( XMLArchivo.c_str() );
		// Obtener el "DOCUMENTO" XML // no es necesario liberar este puntero...
		DOMDocument* xmlDoc = miParser->getDocument();
		// Obtener "Elemento RAIZ" del XML y chequear que sea "<ejecucion>" y que no esté vacío...
		DOMElement* xmlEjecucion = xmlDoc->getDocumentElement();
		if( !(XMLString::equals(xmlEjecucion->getTagName(), XMLString::transcode(NOMBRE_EJECUCION))) ){ // obtengo el ¿NOMBRE=EJECUCION? de la raiz
			cout << "Error: La especificación del XML no corresponde !!!" << endl;
			return 1;
			// NOTA: el XML puede contener otras cosas, pero debe comenzar con "<ejecucion></ejecucion>"	
		}
		if( !xmlEjecucion ){
			cout << "Error: El archivo XML está vacío !!!" << endl;
			return 1;
		}

		// Obtengo los "Nodos HIJOS de la Raiz" y chequeo que no esté vacío // me meto 1 nivel adentro de la RAIZ
		DOMNodeList* xmlHijos = xmlEjecucion->getChildNodes();
		if( !xmlHijos ){
			cout << "Error: El archivo XML está vacío !!!" << endl;
			return 1;
		}
		// Obtengo la "CANTIDAD de Hijos" // debería ser CANTIDAD == 3 (sensores, cajas y actuadores)
		const  XMLSize_t xmlHijosCantidad = xmlHijos->getLength();

		// Me fijo por c/u hijo si es sensor, caja o actuador y lo agrego a vectorAuxiliar
		for( XMLSize_t i = 0; i < xmlHijosCantidad; i++ ){
			DOMNode* xmlNodo = xmlHijos->item(i); // obtengo el HIJO(i)
			if( xmlNodo->getNodeType() && xmlNodo->getNodeType() == DOMNode::ELEMENT_NODE ){ // El Nodo es un elemento => lo re-casteo como un elemento // getNodeType() = TRUE => (es != NULL) // getNodeType() = DOMNode::ELEMENT_NODE => (es un elemento)
				DOMElement* xmlElemento = dynamic_cast< xercesc::DOMElement* >( xmlNodo );
				if( XMLString::equals(xmlElemento->getTagName(), XMLString::transcode(NOMBRE_SENSORES))){ // obtengo el ¿NOMBRE=SONSORES? del elemento y recasteo
					DOMNodeList* xmlSensores = xmlElemento->getChildNodes(); // Obtengo la "LISTA de Sensores"
					const  XMLSize_t xmlSensoresCantidad = xmlSensores->getLength(); // Obtengo la "CANTIDAD de Sensores"
					leer_sensores(xmlSensores, xmlSensoresCantidad, vectorAuxiliar); // Llamo a leer_sensores que llena el vector auxiliar con los sensores
				}
				if( XMLString::equals(xmlElemento->getTagName(), XMLString::transcode(NOMBRE_CAJAS))){ // obtengo el ¿NOMBRE=CAJAS? del elemento y recasteo
					DOMNodeList* xmlCajas = xmlElemento->getChildNodes(); // Obtengo la "LISTA de Cajas"
					const  XMLSize_t xmlCajasCantidad = xmlCajas->getLength(); // Obtengo la "CANTIDAD de Cajas"
					if ( leer_cajas(xmlCajas, xmlCajasCantidad, vectorAuxiliar) ){ // Llamo a leer_cajas que llena el vector auxiliar con las cajas
						cout << "Error: Algo no anduvo bien con las cajas !!!" << endl;
						return 1;
					}
				}
				if( XMLString::equals(xmlElemento->getTagName(), XMLString::transcode(NOMBRE_ACTUADORES))){ // obtengo el ¿NOMBRE=ACTUADORES? del elemento y recasteo
					DOMNodeList* xmlActuadores = xmlElemento->getChildNodes(); // Obtengo la "LISTA de Actuadores"
					const  XMLSize_t xmlActuadoresCantidad = xmlActuadores->getLength(); // Obtengo la "CANTIDAD de Actuadores"
					leer_actuadores(xmlActuadores, xmlActuadoresCantidad, vectorAuxiliar); // Llamo a leer_actuadores que llena el vector auxiliar con los actuadores
				}
			}
		}
	} // end try
	catch (const XMLException& toCatch){
		char* message = XMLString::transcode( toCatch.getMessage() );
		cout << "Error: algún error parseando el archivo: " << message << endl;
		XMLString::release( &message );
		return 1;
	}

	// terminar la infraestructura del Xerces
	terminar_xerces();
	

// 2. CHEQUEAR QUE NO HAYA IDs REPETIDOS
	for (i = 0; i < vectorAuxiliar.size(); i++)
		for (j = 0; j < vectorAuxiliar.size(); j++)
			if ( (j != i) && (vectorAuxiliar[j].id == vectorAuxiliar[i].id) ){
				cout << "Error: hay IDs repetidos !!" << endl;
				return 1;
			}

// 3. CHEQUEAR QUE "ENTRADAS" DE C/U SE CORRESPONDAN CON ELEMENTOS EXISTENTES
	for (i = 0; i < vectorAuxiliar.size(); i++)
		for (j = 0; j < ((vectorAuxiliar[i]).entradas).size(); j++){
			for (k = 0; k < vectorAuxiliar.size(); k++)
				if ( vectorAuxiliar[k].id == vectorAuxiliar[i].entradas[j] )
					break;
			if ( k == vectorAuxiliar.size() ){
				cout << "Error: hay elementos que tienen entradas que no existen !!" << endl;
				return 1;
			}
		}

// 4. TOPOLOGICAL SORTING CON CHEQUEO DE CICLOS
	if ( ordenar_topologicamente(vectorAuxiliar) ){
		cout << "Error: el grafo tiene ciclos !!!" << endl;
		return 1;
	}
	
// 5. ARMO EL "tabla_ejecucion" CON TODOS LOS PUNTEROS
	tabla_ejecucion.clear();
	for (i = 0; i < vectorAuxiliar.size(); i++){
		if ( vectorAuxiliar[i].tipo == TIPO_SENSOR ){ // es un sensor
			nuevoSensor = new Sensor(vectorAuxiliar[i].id); // creo el sensor y lo agrego a TablaEjecucion...
			tabla_ejecucion.push_back(nuevoSensor);
		}
		if ( vectorAuxiliar[i].tipo == TIPO_CAJA ){ // es una caja
			nuevaCaja = new Caja(vectorAuxiliar[i].id); // creo la caja...
			for (j = 0; j < (vectorAuxiliar[i].puntos).size(); j++)
				(*((*nuevaCaja)._puntos)).push_back( (vectorAuxiliar[i]).puntos[j] );
			for (j = 0; j < (vectorAuxiliar[i].entradas).size(); j++)
				for (k = 0; k < i; k++)
					if ( (*(tabla_ejecucion[k])).getId() == (vectorAuxiliar[i]).entradas[j] )
						(*((*nuevaCaja)._entradas)).push_back( tabla_ejecucion[k] ); // agrego el puntero al elemento
			tabla_ejecucion.push_back(nuevaCaja);
		}
		if ( vectorAuxiliar[i].tipo == TIPO_ACTUADOR ){ // es un actuador
			nuevoActuador = new Actuador(vectorAuxiliar[i].id); // creo el actuador...
			for (j = 0; j < (vectorAuxiliar[i].entradas).size(); j++)
				for (k = 0; k < i; k++)
					if ( (*(tabla_ejecucion[k])).getId() == (vectorAuxiliar[i]).entradas[j] )
						(*((*nuevoActuador)._entradas)).push_back( tabla_ejecucion[k] ); // agrego el puntero al elemento
			tabla_ejecucion.push_back(nuevoActuador);
		}
	}

// 6. RETORNO LA "tabla_ejecucion"
	return 0;
}
