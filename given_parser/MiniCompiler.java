
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;
import javax.json.JsonValue;
import java.util.*;

import cfg.*;
import ast.*;
import llvm.*;

public class MiniCompiler
{

   private static boolean stack = false;
   private static boolean jsonPrint = false;
   private static boolean cfg = false;
   private static boolean printLLVM = false;

   public static void main(String[] args) throws TypeCheckException
   {
      parseParameters(args);

      CommonTokenStream tokens = new CommonTokenStream(createLexer());
      MiniParser parser = new MiniParser(tokens);
      ParseTree tree = parser.program();

      ArrayList<CFG> cfgs = new ArrayList<CFG>();


      if (parser.getNumberOfSyntaxErrors() == 0)
      {
         /*
            This visitor will create a JSON representation of the AST.
            This is primarily intended to allow use of languages other
            than Java.  The parser can thusly be used to generate JSON
            and the next phase of the compiler can read the JSON to build
            a language-specific AST representation.
         */
         MiniToJsonVisitor jsonVisitor = new MiniToJsonVisitor();
         JsonValue json = jsonVisitor.visit(tree);
         
         if (jsonPrint) {
            System.out.println(json);
         }

         /*
            This visitor will build an object representation of the AST
            in Java using the provided classes.
         */
         MiniToAstProgramVisitor programVisitor =
            new MiniToAstProgramVisitor();
         ast.Program program = programVisitor.visit(tree);

         // Milestone 1: Static Type Checking

         /* 
         *.    NEED TO FINISH!!
         */

         // CheckRedeclarations.checkProgram(program);
         // MiniTypeChecker.checkProgram(program);

         //Milestone 2 Part 1: Create CFG for each function
         LLVM llvm = new LLVM(program);
         if (cfg) {
            llvm.printProgram();
         }
         if (printLLVM) {
            llvm.printInstructions();
         }
      }
   }

   private static String _inputFile = null;

   private static void parseParameters(String [] args)
   {
      for (int i = 0; i < args.length; i++)
      {
         if (args[i].charAt(0) == '-')
         {
            if (args[i].equals("-stack")) {
               stack = true;
               System.out.println("stack detected");
            }

            else if (args[i].equals("-json")) {
               jsonPrint = true;
            }

            else if (args[i].equals("-cfg")) {
               cfg = true;
            }
            else if (args[i].equals("-printLLVM")) {
               printLLVM = true;
            }
            else {
               System.err.println("unexpected option: " + args[i]);
               System.exit(1);
            }
         }
         else if (_inputFile != null)
         {
            System.err.println("too many files specified");
            System.exit(1);
         }
         else
         {
            _inputFile = args[i];
         }
      }
   }

   private static void error(String msg)
   {
      System.err.println(msg);
      System.exit(1);
   }

   private static MiniLexer createLexer()
   {
      try
      {
         CharStream input;
         if (_inputFile == null)
         {
            input = CharStreams.fromStream(System.in);
         }
         else
         {
            input = CharStreams.fromFileName(_inputFile);
         }
         return new MiniLexer(input);
      }
      catch (java.io.IOException e)
      {
         System.err.println("file not found: " + _inputFile);
         System.exit(1);
         return null;
      }
   }
}
