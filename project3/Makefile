all: compile

compile:
	java -jar ../jtb132di.jar -te minijava.jj
	java -jar ../javacc5.jar minijava-jtb.jj
	javac Offsets.java
	javac Visitor.java
	javac Visitor2.java
	javac Visitor3.java
	javac Method.java
	javac SymbolTable.java
	javac UserClasses.java
	javac GlobalVars.java
	javac Main.java

clean:
	rm -f *.class *~

