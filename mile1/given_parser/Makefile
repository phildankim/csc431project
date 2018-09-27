# Should really move to something else to manage the build.

# Provided code assumes Java 8
JAVAC=javac
JAVA=java
ANTLRJAR=/home/akeen/public/antlr4/antlr-4.7.1-complete.jar
JSONJAR=/home/akeen/public/javax.json-1.0.4.jar

CLASSPATH_MOD=$(ANTLRJAR):$(JSONJAR)

FILES=MiniCompiler.java MiniToJsonVisitor.java MiniToAstDeclarationsVisitor.java MiniToAstExpressionVisitor.java MiniToAstFunctionVisitor.java MiniToAstProgramVisitor.java MiniToAstStatementVisitor.java MiniToAstTypeDeclarationVisitor.java MiniToAstTypeVisitor.java

GENERATED=MiniBaseVisitor.java MiniLexer.java MiniLexer.tokens Mini.tokens MiniVisitor.java MiniParser.java MiniBaseListener.java MiniListener.java

all : MiniCompiler.class

MiniCompiler.class : antlr.generated ${FILES}
	$(JAVAC) -cp ${CLASSPATH}:$(CLASSPATH_MOD) *.java ast/*.java

antlr.generated : Mini.g4
	$(JAVA) -cp ${CLASSPATH}:$(CLASSPATH_MOD) org.antlr.v4.Tool -visitor Mini.g4
	touch antlr.generated

clean:
	\rm -rf *generated* ${GENERATED} *.class ast/*.class
