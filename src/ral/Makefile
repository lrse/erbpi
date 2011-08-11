MAKE=make --no-print-directory --silent

all: 
	@echo "Compilando RALs..."
	@$(MAKE) -C ./yaks
	@$(MAKE) -C ./khepera/cable
	@$(MAKE) -C ./exabot
#	@$(MAKE) -C ./exabotPlayer
	@cp libyaksRAL.so libRAL.so

new: clean all

clean:
	@$(MAKE) clean -C ./yaks
	@$(MAKE) clean -C ./khepera/cable
	@$(MAKE) clean -C ./exabot
	#@$(MAKE) clean -C ./exabotPlayer
	@$(RM) *.so ../lib/*.so
