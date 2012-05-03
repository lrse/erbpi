include Makefile.common
BINDIR=../bin
SRCDIR=../src
LIBDIR=./lib
RALDIR=./ral
GUIDIR=./gui
COREDIR=./core

all: 
	@$(RM) -rf $(BINDIR)
	@$(MAKE) compile install

compile: 
	@echo "Se inicia la compilacion de todo el proyecto..."
	@$(MAKE) -C $(RALDIR)
	@$(MAKE) core -C $(COREDIR)
	@echo "Los ejecutables fueron creados correctamente."
	@echo "Haciendo el .jar del GUI..."
	@$(MAKE) -C $(GUIDIR) compile

install:
	@echo "Creando los directorios para guardar los archivos binarios ..."
	@mkdir $(BINDIR)
	@mkdir $(BINDIR)/core
	@echo "Copiando los comportamientos basicos a la carpeta de archivos binarios..."
	@cp -r ./comportamientos $(BINDIR)
	@$(MAKE) -C $(GUIDIR) install
	@cp run.sh $(BINDIR)/
	@echo "Copiando las distintas RALs a la carpeta de archivos binarios..."
	@cp -r ./ral/*.$(SHARED_EXT) $(BINDIR)/core
	@echo "Copiando el core a la carpeta de archivos binarios..."
	@cp $(COREDIR)/core $(BINDIR)/core/core
	@mkdir $(BINDIR)/player
	@cp -r ./ral/exabot_player/player_config/* $(BINDIR)/player
	
core-exa:
	$(MAKE) -C ral ral-exa
	$(MAKE) -C core core-exa

clean:
	@#~ $(MAKE) clean -C $(GUIDIR)
	@$(MAKE) clean -C $(RALDIR)
	@$(MAKE) clean -C $(COREDIR)
	@echo "Se limpiaron todos los archivos de codigo objeto."

distclean:
	@$(MAKE) clean
	@$(RM) -rf $(BINDIR)
	@echo "Se eliminaron todos los archivos binarios generados."
