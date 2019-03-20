build:
	java -jar antlr-4.7.1-complete.jar IMPGrammar.g4 -visitor
	javac -cp ./antlr-4.7.1-complete.jar *.java

run: CreateAST.class
	java -cp .:antlr-4.7.1-complete.jar CreateAST

clean:
	rm *.class *.interp *.tokens IMPGrammar*.java
