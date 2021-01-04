// Generated from com/coolbeevip/antlr/Log.g4 by ANTLR 4.7.1
package com.coolbeevip.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LogParser}.
 */
public interface LogListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LogParser#log}.
	 * @param ctx the parse tree
	 */
	void enterLog(LogParser.LogContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogParser#log}.
	 * @param ctx the parse tree
	 */
	void exitLog(LogParser.LogContext ctx);
	/**
	 * Enter a parse tree produced by {@link LogParser#entry}.
	 * @param ctx the parse tree
	 */
	void enterEntry(LogParser.EntryContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogParser#entry}.
	 * @param ctx the parse tree
	 */
	void exitEntry(LogParser.EntryContext ctx);
	/**
	 * Enter a parse tree produced by {@link LogParser#timestamp}.
	 * @param ctx the parse tree
	 */
	void enterTimestamp(LogParser.TimestampContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogParser#timestamp}.
	 * @param ctx the parse tree
	 */
	void exitTimestamp(LogParser.TimestampContext ctx);
	/**
	 * Enter a parse tree produced by {@link LogParser#level}.
	 * @param ctx the parse tree
	 */
	void enterLevel(LogParser.LevelContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogParser#level}.
	 * @param ctx the parse tree
	 */
	void exitLevel(LogParser.LevelContext ctx);
	/**
	 * Enter a parse tree produced by {@link LogParser#message}.
	 * @param ctx the parse tree
	 */
	void enterMessage(LogParser.MessageContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogParser#message}.
	 * @param ctx the parse tree
	 */
	void exitMessage(LogParser.MessageContext ctx);
}