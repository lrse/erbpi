include ../Makefile.common

RALS := yaks_ral

ifeq ($(LINUX), 1)
  RALS += khepera_ral exabot_player_ral
endif

yaks_ral:
	$(MAKE) -C yaks
	cp libyaksRAL.$(SHARED_EXT) libRAL.$(SHARED_EXT)
	
khepera_ral:
	$(MAKE) -C khepera/cable
	
exabot_player_ral:
	$(MAKE) -C exabot_player
	
	
all: $(RALS)
	
ral-exa:
	$(MAKE) -C exabot

new: clean all

clean:
	$(MAKE) clean -C yaks
	$(MAKE) clean -C khepera/cable
	$(MAKE) clean -C exabot
	$(MAKE) clean -C exabot_player
	$(RM) *.$(SHARED_EXT) ../lib/*.$(SHARED_EXT)
