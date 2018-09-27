grammar Mini;

@header
{
   /* package declaration here */
}

/*
   Parser Rules
*/

program
   :  types declarations functions EOF
   ;
types
   :  typeDeclaration*
   |  
   ;
typeDeclaration
   :  'struct' ID '{' nestedDecl '}' ';'
   ;
nestedDecl
   :  (decl ';')+
   ;
decl
   :  type ID
   ;
type
   :  'int'             # IntType
   |  'bool'            # BoolType
   |  'struct' ID       # StructType
   ;
declarations
   :  (declaration)*
   ;
declaration
   :  type ID (',' ID)* ';'
   ;
functions
   :  function*
   ;
function
   :  'fun' ID parameters returnType '{' declarations statementList '}'
   ;
parameters
   :  '(' (decl (',' decl)*)? ')'
   ;
returnType
   :  type           # ReturnTypeReal
   |  'void'         # ReturnTypeVoid
   ;
statement
   :  block                                           # NestedBlock
   |  lvalue '=' (expression | 'read') ';'            # Assignment
   |  'print' expression ';'                          # Print
   |  'print' expression 'endl' ';'                   # PrintLn
   |  'if' '(' expression ')' thenBlock=block
         ('else' elseBlock=block)?                    # Conditional
   |  'while' '(' expression ')' block                # While
   |  'delete' expression ';'                         # Delete
   |  'return' (expression)? ';'                      # Return
   |  ID '(' arguments ')' ';'                        # Invocation
   ;
block
   :  '{' statementList '}'
   ;
statementList
   :  statement*
   ;
lvalue
   :  ID                                              # LvalueId
   |  lvalue '.' ID                                   # LvalueDot
   ;
expression
   :  ID '(' arguments ')'                               # InvocationExpr
   |  expression ('.' ID)                                # DotExpr
   |  op=('-' | '!') expression                          # UnaryExpr
   |  lft=expression op=('*' | '/') rht=expression                # BinaryExpr
   |  lft=expression op=('+' | '-') rht=expression                # BinaryExpr
   |  lft=expression op=('<' | '>' | '<=' | '>=') rht=expression  # BinaryExpr
   |  lft=expression op=('==' | '!=') rht=expression              # BinaryExpr
   |  lft=expression op='&&' rht=expression                       # BinaryExpr
   |  lft=expression op='||' rht=expression                       # BinaryExpr
   |  ID                                                 # IdentifierExpr
   |  INTEGER                                            # IntegerExpr
   |  'true'                                             # TrueExpr
   |  'false'                                            # FalseExpr
   |  'new' ID                                           # NewExpr
   |  'null'                                             # NullExpr
   |  '(' expression ')'                                 # NestedExpr
   ;
arguments
   :  (expression (',' expression)*)?
   ;

/*
   Lexer Rules
*/

ID       :  [a-zA-Z][a-zA-Z0-9]* ;

INTEGER  :  '0' | [1-9][0-9]* ;

WS       :  [ \t\n\r\f]+ -> skip; 

COMMENT  :  '#' .*? '\n' -> skip;
