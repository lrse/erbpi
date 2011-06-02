MAKE=make --no-print-directory --silent

all: 
	@$(MAKE) -C ./lnx
	@$(MAKE) -C ./win
	@mv ./lnx/*.o ./
	@mv ./win/*.o ./
	
new: clean all

lnx:
	@$(MAKE) -C ./lnx
	
win:
	@$(MAKE) -C ./win
		
clean:
	@$(MAKE) clean -C ./lnx
	@$(MAKE) clean -C ./win
	@$(RM) *.so
