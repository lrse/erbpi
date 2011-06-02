MAKE=make --no-print-directory --silent

all: 
	@$(MAKE) -C ./yaks
	@$(MAKE) -C ./khepera/cable
	@$(MAKE) -C ./exabot
	#@$(MAKE) -C ./exabotPlayer

new: clean all

clean:
	@$(MAKE) clean -C ./yaks
	@$(MAKE) clean -C ./khepera/cable
	@$(MAKE) clean -C ./exabot
	#@$(MAKE) clean -C ./exabotPlayer
	@$(RM) *.so
