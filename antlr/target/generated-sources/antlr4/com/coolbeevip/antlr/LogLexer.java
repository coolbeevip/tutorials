// Generated from com/coolbeevip/antlr/Log.g4 by ANTLR 4.7.1
package com.coolbeevip.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LogLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, DATE=5, TIME=6, TEXT=7, CRLF=8;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "DIGIT", "TWODIGIT", "LETTER", "DATE", 
		"TIME", "TEXT", "CRLF"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "' '", "'ERROR'", "'INFO'", "'DEBUG'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, "DATE", "TIME", "TEXT", "CRLF"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public LogLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Log.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\nN\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\6\13D\n\13\r\13\16\13E\3\f\5"+
		"\fI\n\f\3\f\3\f\5\fM\n\f\2\2\r\3\3\5\4\7\5\t\6\13\2\r\2\17\2\21\7\23\b"+
		"\25\t\27\n\3\2\4\3\2\62;\4\2C\\c|\2M\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2"+
		"\2\2\t\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\3\31"+
		"\3\2\2\2\5\33\3\2\2\2\7!\3\2\2\2\t&\3\2\2\2\13,\3\2\2\2\r.\3\2\2\2\17"+
		"\61\3\2\2\2\21\63\3\2\2\2\23<\3\2\2\2\25C\3\2\2\2\27L\3\2\2\2\31\32\7"+
		"\"\2\2\32\4\3\2\2\2\33\34\7G\2\2\34\35\7T\2\2\35\36\7T\2\2\36\37\7Q\2"+
		"\2\37 \7T\2\2 \6\3\2\2\2!\"\7K\2\2\"#\7P\2\2#$\7H\2\2$%\7Q\2\2%\b\3\2"+
		"\2\2&\'\7F\2\2\'(\7G\2\2()\7D\2\2)*\7W\2\2*+\7I\2\2+\n\3\2\2\2,-\t\2\2"+
		"\2-\f\3\2\2\2./\5\13\6\2/\60\5\13\6\2\60\16\3\2\2\2\61\62\t\3\2\2\62\20"+
		"\3\2\2\2\63\64\5\r\7\2\64\65\5\r\7\2\65\66\7/\2\2\66\67\5\17\b\2\678\5"+
		"\17\b\289\5\17\b\29:\7/\2\2:;\5\r\7\2;\22\3\2\2\2<=\5\r\7\2=>\7<\2\2>"+
		"?\5\r\7\2?@\7<\2\2@A\5\r\7\2A\24\3\2\2\2BD\5\17\b\2CB\3\2\2\2DE\3\2\2"+
		"\2EC\3\2\2\2EF\3\2\2\2F\26\3\2\2\2GI\7\17\2\2HG\3\2\2\2HI\3\2\2\2IJ\3"+
		"\2\2\2JM\7\f\2\2KM\7\17\2\2LH\3\2\2\2LK\3\2\2\2M\30\3\2\2\2\6\2EHL\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}