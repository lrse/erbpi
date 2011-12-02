all: 
	@echo "Compilando RALs..."
	$(MAKE) -C yaks
	$(MAKE) -C khepera/cable
#	$(MAKE) -C exabot_player
	cp libyaksRAL.so libRAL.so
	
ral-exa:
	$(MAKE) -C exabot

new: clean all

clean:
	$(MAKE) clean -C yaks
	$(MAKE) clean -C khepera/cable
	$(MAKE) clean -C exabot
#	$(MAKE) clean -C exabot_player
	$(RM) *.so ../lib/*.so
