BINDIR=../bin
SRCDIR=../src
LIBDIR=./lib
RALDIR=./ral
GUIDIR=./gui
COREDIR=./core

MAKE=make

all: 
	@$(RM) -rf $(BINDIR)
	@$(MAKE) compile install

compile: 
	@echo "Se inicia la compilacion de todo el proyecto..."
	@#echo "Compilando la GUI..."
	@#~ $(MAKE) -C $(GUIDIR)
	@$(MAKE) -C $(RALDIR)
	@$(MAKE) core -C $(COREDIR)
	@echo "Los ejecutables fueron creados correctamente."

install:
	@echo "Creando los directorios para guardar los archivos binarios ..."
	@mkdir $(BINDIR)
	@mkdir $(BINDIR)/core
	@echo "Copiando los comportamientos basicos a la carpeta de archivos binarios..."
	@cp -r ./comportamientos $(BINDIR)
	@cp -r ./gui $(BINDIR)
	@echo "Copiando las distintas RALs a la carpeta de archivos binarios..."
	@cp -r ./ral/*.so $(BINDIR)/core
	@echo "Copiando el core a la carpeta de archivos binarios..."
	@cp $(COREDIR)/core $(BINDIR)/core/core_exe
	@cp $(COREDIR)/core.sh $(BINDIR)/core/

clean:
	@#~ $(MAKE) clean -C $(GUIDIR)
	@$(MAKE) clean -C $(RALDIR)
	@$(MAKE) clean -C $(COREDIR)
	@echo "Se limpiaron todos los archivos de codigo objeto."

distclean:
	@$(MAKE) clean
	@$(RM) -rf $(BINDIR)
	@echo "Se eliminaron todos los archivos binarios generados."
