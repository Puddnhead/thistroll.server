lexer grammar SpeechLexerRules;

WORD: [A-Za-z]+;
WS: (' '|'\n'|'\r'|'\t')+   -> skip;