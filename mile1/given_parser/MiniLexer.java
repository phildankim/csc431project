// Generated from Mini.g4 by ANTLR 4.7.1

   /* package declaration here */

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MiniLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		ID=39, INTEGER=40, WS=41, COMMENT=42;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
		"T__25", "T__26", "T__27", "T__28", "T__29", "T__30", "T__31", "T__32", 
		"T__33", "T__34", "T__35", "T__36", "T__37", "ID", "INTEGER", "WS", "COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'struct'", "'{'", "'}'", "';'", "'int'", "'bool'", "','", "'fun'", 
		"'('", "')'", "'void'", "'='", "'read'", "'print'", "'endl'", "'if'", 
		"'else'", "'while'", "'delete'", "'return'", "'.'", "'-'", "'!'", "'*'", 
		"'/'", "'+'", "'<'", "'>'", "'<='", "'>='", "'=='", "'!='", "'&&'", "'||'", 
		"'true'", "'false'", "'new'", "'null'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, "ID", "INTEGER", "WS", "COMMENT"
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


	public MiniLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Mini.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2,\u0103\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\6\3\7"+
		"\3\7\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f"+
		"\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32"+
		"\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3\37\3 \3"+
		" \3 \3!\3!\3!\3\"\3\"\3\"\3#\3#\3#\3$\3$\3$\3$\3$\3%\3%\3%\3%\3%\3%\3"+
		"&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3(\3(\7(\u00e3\n(\f(\16(\u00e6\13(\3)\3"+
		")\3)\7)\u00eb\n)\f)\16)\u00ee\13)\5)\u00f0\n)\3*\6*\u00f3\n*\r*\16*\u00f4"+
		"\3*\3*\3+\3+\7+\u00fb\n+\f+\16+\u00fe\13+\3+\3+\3+\3+\3\u00fc\2,\3\3\5"+
		"\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21"+
		"!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!"+
		"A\"C#E$G%I&K\'M(O)Q*S+U,\3\2\7\4\2C\\c|\5\2\62;C\\c|\3\2\63;\3\2\62;\5"+
		"\2\13\f\16\17\"\"\2\u0107\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2"+
		"\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2"+
		"\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\3W\3\2\2\2\5^\3\2\2\2\7`\3\2\2\2\tb"+
		"\3\2\2\2\13d\3\2\2\2\rh\3\2\2\2\17m\3\2\2\2\21o\3\2\2\2\23s\3\2\2\2\25"+
		"u\3\2\2\2\27w\3\2\2\2\31|\3\2\2\2\33~\3\2\2\2\35\u0083\3\2\2\2\37\u0089"+
		"\3\2\2\2!\u008e\3\2\2\2#\u0091\3\2\2\2%\u0096\3\2\2\2\'\u009c\3\2\2\2"+
		")\u00a3\3\2\2\2+\u00aa\3\2\2\2-\u00ac\3\2\2\2/\u00ae\3\2\2\2\61\u00b0"+
		"\3\2\2\2\63\u00b2\3\2\2\2\65\u00b4\3\2\2\2\67\u00b6\3\2\2\29\u00b8\3\2"+
		"\2\2;\u00ba\3\2\2\2=\u00bd\3\2\2\2?\u00c0\3\2\2\2A\u00c3\3\2\2\2C\u00c6"+
		"\3\2\2\2E\u00c9\3\2\2\2G\u00cc\3\2\2\2I\u00d1\3\2\2\2K\u00d7\3\2\2\2M"+
		"\u00db\3\2\2\2O\u00e0\3\2\2\2Q\u00ef\3\2\2\2S\u00f2\3\2\2\2U\u00f8\3\2"+
		"\2\2WX\7u\2\2XY\7v\2\2YZ\7t\2\2Z[\7w\2\2[\\\7e\2\2\\]\7v\2\2]\4\3\2\2"+
		"\2^_\7}\2\2_\6\3\2\2\2`a\7\177\2\2a\b\3\2\2\2bc\7=\2\2c\n\3\2\2\2de\7"+
		"k\2\2ef\7p\2\2fg\7v\2\2g\f\3\2\2\2hi\7d\2\2ij\7q\2\2jk\7q\2\2kl\7n\2\2"+
		"l\16\3\2\2\2mn\7.\2\2n\20\3\2\2\2op\7h\2\2pq\7w\2\2qr\7p\2\2r\22\3\2\2"+
		"\2st\7*\2\2t\24\3\2\2\2uv\7+\2\2v\26\3\2\2\2wx\7x\2\2xy\7q\2\2yz\7k\2"+
		"\2z{\7f\2\2{\30\3\2\2\2|}\7?\2\2}\32\3\2\2\2~\177\7t\2\2\177\u0080\7g"+
		"\2\2\u0080\u0081\7c\2\2\u0081\u0082\7f\2\2\u0082\34\3\2\2\2\u0083\u0084"+
		"\7r\2\2\u0084\u0085\7t\2\2\u0085\u0086\7k\2\2\u0086\u0087\7p\2\2\u0087"+
		"\u0088\7v\2\2\u0088\36\3\2\2\2\u0089\u008a\7g\2\2\u008a\u008b\7p\2\2\u008b"+
		"\u008c\7f\2\2\u008c\u008d\7n\2\2\u008d \3\2\2\2\u008e\u008f\7k\2\2\u008f"+
		"\u0090\7h\2\2\u0090\"\3\2\2\2\u0091\u0092\7g\2\2\u0092\u0093\7n\2\2\u0093"+
		"\u0094\7u\2\2\u0094\u0095\7g\2\2\u0095$\3\2\2\2\u0096\u0097\7y\2\2\u0097"+
		"\u0098\7j\2\2\u0098\u0099\7k\2\2\u0099\u009a\7n\2\2\u009a\u009b\7g\2\2"+
		"\u009b&\3\2\2\2\u009c\u009d\7f\2\2\u009d\u009e\7g\2\2\u009e\u009f\7n\2"+
		"\2\u009f\u00a0\7g\2\2\u00a0\u00a1\7v\2\2\u00a1\u00a2\7g\2\2\u00a2(\3\2"+
		"\2\2\u00a3\u00a4\7t\2\2\u00a4\u00a5\7g\2\2\u00a5\u00a6\7v\2\2\u00a6\u00a7"+
		"\7w\2\2\u00a7\u00a8\7t\2\2\u00a8\u00a9\7p\2\2\u00a9*\3\2\2\2\u00aa\u00ab"+
		"\7\60\2\2\u00ab,\3\2\2\2\u00ac\u00ad\7/\2\2\u00ad.\3\2\2\2\u00ae\u00af"+
		"\7#\2\2\u00af\60\3\2\2\2\u00b0\u00b1\7,\2\2\u00b1\62\3\2\2\2\u00b2\u00b3"+
		"\7\61\2\2\u00b3\64\3\2\2\2\u00b4\u00b5\7-\2\2\u00b5\66\3\2\2\2\u00b6\u00b7"+
		"\7>\2\2\u00b78\3\2\2\2\u00b8\u00b9\7@\2\2\u00b9:\3\2\2\2\u00ba\u00bb\7"+
		">\2\2\u00bb\u00bc\7?\2\2\u00bc<\3\2\2\2\u00bd\u00be\7@\2\2\u00be\u00bf"+
		"\7?\2\2\u00bf>\3\2\2\2\u00c0\u00c1\7?\2\2\u00c1\u00c2\7?\2\2\u00c2@\3"+
		"\2\2\2\u00c3\u00c4\7#\2\2\u00c4\u00c5\7?\2\2\u00c5B\3\2\2\2\u00c6\u00c7"+
		"\7(\2\2\u00c7\u00c8\7(\2\2\u00c8D\3\2\2\2\u00c9\u00ca\7~\2\2\u00ca\u00cb"+
		"\7~\2\2\u00cbF\3\2\2\2\u00cc\u00cd\7v\2\2\u00cd\u00ce\7t\2\2\u00ce\u00cf"+
		"\7w\2\2\u00cf\u00d0\7g\2\2\u00d0H\3\2\2\2\u00d1\u00d2\7h\2\2\u00d2\u00d3"+
		"\7c\2\2\u00d3\u00d4\7n\2\2\u00d4\u00d5\7u\2\2\u00d5\u00d6\7g\2\2\u00d6"+
		"J\3\2\2\2\u00d7\u00d8\7p\2\2\u00d8\u00d9\7g\2\2\u00d9\u00da\7y\2\2\u00da"+
		"L\3\2\2\2\u00db\u00dc\7p\2\2\u00dc\u00dd\7w\2\2\u00dd\u00de\7n\2\2\u00de"+
		"\u00df\7n\2\2\u00dfN\3\2\2\2\u00e0\u00e4\t\2\2\2\u00e1\u00e3\t\3\2\2\u00e2"+
		"\u00e1\3\2\2\2\u00e3\u00e6\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4\u00e5\3\2"+
		"\2\2\u00e5P\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e7\u00f0\7\62\2\2\u00e8\u00ec"+
		"\t\4\2\2\u00e9\u00eb\t\5\2\2\u00ea\u00e9\3\2\2\2\u00eb\u00ee\3\2\2\2\u00ec"+
		"\u00ea\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00f0\3\2\2\2\u00ee\u00ec\3\2"+
		"\2\2\u00ef\u00e7\3\2\2\2\u00ef\u00e8\3\2\2\2\u00f0R\3\2\2\2\u00f1\u00f3"+
		"\t\6\2\2\u00f2\u00f1\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4"+
		"\u00f5\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6\u00f7\b*\2\2\u00f7T\3\2\2\2\u00f8"+
		"\u00fc\7%\2\2\u00f9\u00fb\13\2\2\2\u00fa\u00f9\3\2\2\2\u00fb\u00fe\3\2"+
		"\2\2\u00fc\u00fd\3\2\2\2\u00fc\u00fa\3\2\2\2\u00fd\u00ff\3\2\2\2\u00fe"+
		"\u00fc\3\2\2\2\u00ff\u0100\7\f\2\2\u0100\u0101\3\2\2\2\u0101\u0102\b+"+
		"\2\2\u0102V\3\2\2\2\b\2\u00e4\u00ec\u00ef\u00f4\u00fc\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}