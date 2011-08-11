#ifndef _PARSER_H_
#define _PARSER_H_

	// defino los nombres de las entidades y atributos que estarán en el XML...
	#define NOMBRE_EJECUCION "ejecucion"
	#define NOMBRE_SENSORES "sensores"
	#define NOMBRE_SENSOR "sensor"
	#define NOMBRE_CAJAS "cajas"
	#define NOMBRE_CAJA "caja"
	#define NOMBRE_ACTUADORES "actuadores"
	#define NOMBRE_ACTUADOR "actuador"
	#define NOMBRE_ATRIBUTO_ID "id"
	#define NOMBRE_ENTRADAS "entradas"
	#define NOMBRE_ENTRADA "entrada"
	#define NOMBRE_PUNTOS "puntos"
	#define NOMBRE_PUNTO "punto"
	#define NOMBRE_ATRIBUTO_PUNTO_X "x"
	#define NOMBRE_ATRIBUTO_PUNTO_Y "y"
	
	// incluir las cosas para el manejo de errores de archivos
	#include <sys/stat.h>
	#include <errno.h>
	// incluir las cosas estandar...
	#include <string>
	#include <vector>
	#include <iostream>
	using namespace std;
	// incluir las estructuras a usar...
	#include "Estructuras.h"

	// incluir las cosas del la DOM API
	#include <xercesc/dom/DOMElement.hpp>
	#include <xercesc/dom/DOMNodeList.hpp>
	#include <xercesc/parsers/XercesDOMParser.hpp>
	// incluir las cosas del Parser XML: Xerces-C++ version 3.0.1
	#include <xercesc/util/PlatformUtils.hpp>

	// usar namespace para Parser XML
	using namespace xercesc;

	int parsear(string XMLArchivo, vector<Elemento*>& tabla_ejecucion);	

#endif // _PARSER_H_
