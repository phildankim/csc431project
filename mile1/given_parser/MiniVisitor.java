// Generated from Mini.g4 by ANTLR 4.7.1

   /* package declaration here */

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MiniParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MiniVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MiniParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MiniParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniParser#types}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypes(MiniParser.TypesContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniParser#typeDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDeclaration(MiniParser.TypeDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniParser#nestedDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNestedDecl(MiniParser.NestedDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl(MiniParser.DeclContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntType}
	 * labeled alternative in {@link MiniParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntType(MiniParser.IntTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BoolType}
	 * labeled alternative in {@link MiniParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolType(MiniParser.BoolTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StructType}
	 * labeled alternative in {@link MiniParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructType(MiniParser.StructTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniParser#declarations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarations(MiniParser.DeclarationsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(MiniParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniParser#functions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctions(MiniParser.FunctionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(MiniParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniParser#parameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameters(MiniParser.ParametersContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReturnTypeReal}
	 * labeled alternative in {@link MiniParser#returnType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnTypeReal(MiniParser.ReturnTypeRealContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReturnTypeVoid}
	 * labeled alternative in {@link MiniParser#returnType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnTypeVoid(MiniParser.ReturnTypeVoidContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NestedBlock}
	 * labeled alternative in {@link MiniParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNestedBlock(MiniParser.NestedBlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Assignment}
	 * labeled alternative in {@link MiniParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(MiniParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Print}
	 * labeled alternative in {@link MiniParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrint(MiniParser.PrintContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PrintLn}
	 * labeled alternative in {@link MiniParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintLn(MiniParser.PrintLnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Conditional}
	 * labeled alternative in {@link MiniParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditional(MiniParser.ConditionalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code While}
	 * labeled alternative in {@link MiniParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile(MiniParser.WhileContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Delete}
	 * labeled alternative in {@link MiniParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelete(MiniParser.DeleteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Return}
	 * labeled alternative in {@link MiniParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn(MiniParser.ReturnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Invocation}
	 * labeled alternative in {@link MiniParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInvocation(MiniParser.InvocationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(MiniParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniParser#statementList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementList(MiniParser.StatementListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LvalueId}
	 * labeled alternative in {@link MiniParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLvalueId(MiniParser.LvalueIdContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LvalueDot}
	 * labeled alternative in {@link MiniParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLvalueDot(MiniParser.LvalueDotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntegerExpr}
	 * labeled alternative in {@link MiniParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerExpr(MiniParser.IntegerExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TrueExpr}
	 * labeled alternative in {@link MiniParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrueExpr(MiniParser.TrueExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IdentifierExpr}
	 * labeled alternative in {@link MiniParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifierExpr(MiniParser.IdentifierExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BinaryExpr}
	 * labeled alternative in {@link MiniParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryExpr(MiniParser.BinaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NewExpr}
	 * labeled alternative in {@link MiniParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewExpr(MiniParser.NewExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NestedExpr}
	 * labeled alternative in {@link MiniParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNestedExpr(MiniParser.NestedExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DotExpr}
	 * labeled alternative in {@link MiniParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDotExpr(MiniParser.DotExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnaryExpr}
	 * labeled alternative in {@link MiniParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpr(MiniParser.UnaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InvocationExpr}
	 * labeled alternative in {@link MiniParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInvocationExpr(MiniParser.InvocationExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FalseExpr}
	 * labeled alternative in {@link MiniParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalseExpr(MiniParser.FalseExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NullExpr}
	 * labeled alternative in {@link MiniParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullExpr(MiniParser.NullExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniParser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(MiniParser.ArgumentsContext ctx);
}