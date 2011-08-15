MAKE=make --no-print-directory --silent

all: 
	@echo "Compilando RALs..."
	@$(MAKE) -C ./yaks
	@$(MAKE) -C ./khepera/cable
	@$(MAKE) -C ./exabot
	@$(MAKE) -C ./exabot_player
	@cp libyaksRAL.so libRAL.so

new: clean all

clean:
	@$(MAKE) clean -C ./yaks
	@$(MAKE) clean -C ./khepera/cable
	@$(MAKE) clean -C ./exabot
	@$(MAKE) clean -C ./exabot_player
	@$(RM) *.so ../lib/*.so
