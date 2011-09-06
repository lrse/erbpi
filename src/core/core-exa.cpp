// incluir las cosas estandar...
#include <fstream>
#include <iostream>
#include <signal.h>
#include <boost/asio.hpp>
#include <cstdlib>
#include <vector>
#include <list>
#include <sys/types.h>
#include <stdint.h>
#include "core.h"
using namespace std;
using boost::asio::ip::tcp;

// PROTOTIPO DE FUNCIONES AUXILIARES
bool debo_terminar, started;
void terminar(int sig); 					// rutina de atención de SIGNAL

size_t expect_command(const string& delimiter, boost::asio::streambuf& buffer, boost::asio::io_service& io_service,
  tcp::acceptor& acceptor)
{
  tcp::socket socket(io_service);
  acceptor.non_blocking(true);
  boost::system::error_code err; 
  acceptor.accept(socket, err);
  if (err  == boost::asio::error::would_block) return 0;
  return boost::asio::read_until(socket, buffer, delimiter);
}

// MAIN !!
int main(int argc, char** argv)
{
  if (argc != 2) {
    cerr << "Uso: core-exa <archivo de log>" << endl;
    return 1;
  }
  
  signal(SIGINT, terminar);
  
  cout << "inicializando" << endl;
  core_initialize(argv[1]);

  started = debo_terminar = false;
  
  boost::asio::io_service io_service;
  tcp::acceptor acceptor(io_service, tcp::endpoint(tcp::v4(), 7654));
  acceptor.listen();
    
  while(!debo_terminar) {
    try {
      boost::asio::streambuf buffer;
      std::istream is(&buffer);
      is.unsetf(std::ios_base::skipws);

      if (!started) {
        size_t n = expect_command("</ejecucion>\n", buffer, io_service, acceptor);
        if (n == 0) { usleep(10000); continue; }
      
        vector<char> xml_buf(n);
        is.read(&xml_buf[0], n);
        cout << "iniciando ejecucion" << endl;
        core_start(string(&xml_buf[0], n));
        started = true;
      }
      else if (started) {
        core_execute();
          
        size_t n = expect_command("STOP\n", buffer, io_service, acceptor);
        if (n == 0) continue;
        
        cout << "terminando ejecucion" << endl;          
        core_stop();
        started = false;
      }
    }
    catch (std::exception& e) {
      std::cerr << e.what() << std::endl;
    }
  }  
  
  if (started) { 
    cout << "terminando ejecucion" << endl;
    core_stop();
  }
  
  cout << "apagando" << endl;
  core_deinitialize();
  
  return 0;
}

// DEFINICION DE FUNCIONES AUXILIARES
void terminar(int sig){
  debo_terminar = true;
}
