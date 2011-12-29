compile:
	# creo el JAR del proyecto GUI-Java
	rm -f gui.jar
	jar cvfm gui.jar MANIFEST.MF extension thirdparty org

install:
	mkdir -p ../../bin/gui
	mv gui.jar ../../bin/gui
	chmod a+x ../../bin/gui/gui.jar
