compile:
	# crea el JAR, excluyendo los .svn y los .java
	rm -f gui.jar
	find extension org thirdparty -not -path '*.svn*' -not -iname '*.java' -not -type d | xargs jar cvfm gui.jar MANIFEST.MF 
	jar i gui.jar

install:
	mkdir -p ../../bin/gui
	mv gui.jar ../../bin/gui
	chmod a+x ../../bin/gui/gui.jar
