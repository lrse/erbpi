SRCDIR=../src
BINDIR=../bin
WINDIR=../bin/win
LNXDIR=../bin/lnx

LIBDIR=./lib
WINRALDIR=./ral/win
LNXRALDIR=./ral/lnx
COREDIR=./core

MAKE=make --no-print-directory --silent

all: 
	@echo "Se inicia la compilacion de todo el proyecto..."
	@$(MAKE) win
	@$(MAKE) lnx
	@echo "Listo!"

new: 
	@$(MAKE) distclean
	@$(MAKE) common
	@$(MAKE) all

common:
	@echo "Creando los directorios para guardar los archivos binarios ..."
	@mkdir $(BINDIR)
	@mkdir $(WINDIR)
	@mkdir $(WINDIR)/lib
	@mkdir $(LNXDIR)
	@mkdir $(LNXDIR)/lib

	@echo "Copiando los comportamientos basicos a la carpeta de archivos binarios..."
	@cp -r ./comportamientos $(WINDIR)
	@cp -r ./comportamientos $(LNXDIR)

	@echo "Compilando la GUI..."
	@#~ $(MAKE) gtk -C ./libs
	@#~ $(MAKE) gui -C ./gui

win:
	@echo "Compilando el codigo fuente para Windows..."
	@$(MAKE) winxerces -C $(LIBDIR)
	@#$(MAKE) -C $(WINRALDIR)
	@#$(MAKE) win -C $(COREDIR)
	
	@echo "Moviendo los archivos binarios generados a la carpeta de archivos binarios para Windows... "
	@#mv $(WINRALDIR)/*.dll $(DIRWIN)/lib
	@#mv $(COREDIR)/*.exe $(DIRWIN)
	
lnx:
	@echo "Compilando el codigo fuente para Linux..."
	@$(MAKE) lnxxerces -C $(LIBDIR)
	@$(MAKE) -C $(LNXRALDIR)
	@#~ $(MAKE) lnx -C $(COREDIR)

	@echo "Moviendo los archivos binarios generados a la carpeta de archivos binarios para Linux... "
	@mv $(LNXRALDIR)/*.so $(DIRLNX)/lib
	@#mv $(COREDIR)/core $(LNXDIR)

cleanwin:
	@echo "Eliminando archivos de compilacion de las RAL de Windows..."
	@$(MAKE) clean -C $(LNXRALDIR)
	
cleanlnx:
	@echo "Eliminando archivos compilacion de las RAL de Linux..."
	@$(MAKE) clean -C $(LNXRALDIR)
	
clean:
	@$(MAKE) cleanwin
	@$(MAKE) cleanlnx
	@echo "Eliminando archivos de compilacion de las librerias..."
	@$(MAKE) -C $(LIBDIR) clean

distclean:
	@$(MAKE) clean
	@echo "Limpiando carpeta de archivos binarios..."
	@$(RM) -rf $(BINDIR)
