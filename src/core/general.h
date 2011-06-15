#ifndef _GENERAL_H_
#define _GENERAL_H_

/* common includes */

#include <vector>

/* Useful defines */

#define forn(i,n) for(int i=0;i<n;i++)
#define fornr(i,n) for(int i=n-1;0<=i;i--)
#define forsn(i,s,n) for(int i=s;i<n;i++)
#define forsnr(i,s,n) for(int i=n-1;s<=i;i--)
#define forall(it,X) for(typeof((X).begin()) it=(X).begin();it!=(X).end();it++)
#define forallr(it,X) for(typeof((X).rbegin()) it=(X).rbegin();it!=(X).rend();it++)

// estructura auxiliar...
struct Item {
  std::string id;
  int valor;
};

typedef std::vector<std::string> ListaDeSensores;
typedef std::vector<std::string> ListaDeActuadores;

typedef std::vector<Item> EstadoDeSensores;
typedef std::vector<Item> EstadoDeActuadores;

#endif
