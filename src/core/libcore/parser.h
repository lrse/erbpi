#ifndef _PARSER_H_
#define _PARSER_H_
	
	// incluir las cosas para el manejo de errores de archivos
	#include <sys/stat.h>
	#include <errno.h>
	// incluir las cosas estandar...
	#include <string>
	#include <vector>
	#include <iostream>
	using namespace std;
	// incluir las estructuras a usar...
	#include "estructuras.h"

	// incluir las cosas del la DOM API
	#include <xercesc/dom/DOMElement.hpp>
	#include <xercesc/dom/DOMNodeList.hpp>
	#include <xercesc/parsers/XercesDOMParser.hpp>
	// incluir las cosas del Parser XML: Xerces-C++ version 3.0.1
	#include <xercesc/util/PlatformUtils.hpp>
  #include <xercesc/framework/MemBufInputSource.hpp> 

	// usar namespace para Parser XML
	using namespace xercesc;

  void init_parser(void); 
  void deinit_parser(void); 
	int parsear(string XMLArchivo, Conducta* consucta);	

#endif // _PARSER_H_
