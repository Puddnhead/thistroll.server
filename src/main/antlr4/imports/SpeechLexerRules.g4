lexer grammar SpeechLexerRules;

// Punctuation
QUESTION_MARK: '?';

// conditional
IF: 'if';

// question words
AM: 'am';
ARE: 'are';
CAN: 'can';
COULD: 'could';
DO: 'do';
DOES: 'does';
DID: 'did';
HOW: 'how';
IS: 'is';
OUGHT: 'ought';
SHOULD: 'should';
WAS: 'was';
WERE: 'were';
WHAT: 'what';
WHEN: 'when';
WHERE: 'where';
WHICH: 'which';
WHO: 'who';
WHOM: 'whom';
WHOSE: 'whose';
WHY: 'why';
WILL: 'will';
WOULD: 'would';

// Prepositions
ABOARD: 'aboard';
ABOUT: 'about';
ABOVE: 'above';
ABSENT: 'absent';
ACROSS: 'across';
AFTER: 'after';
AGAINST: 'against';
ALONGSIDE: 'alongside';
ALONG: 'along';
AMID: 'amid';
AMONGST: 'amongst';
AMONG: 'among';
AROUND: 'around';
ASTRIDE: 'astride';
AS: 'as';
ATOP: 'atop';
BEFORE: 'before';
BELOW: 'below';
BESIDES: 'besides';
BESIDE: 'beside';
BETWEEN: 'between';
BEYOND: 'beyond';
BY: 'by';
DESPITE: 'despite';
DOWN: 'down';
DURING: 'during';
EXCEPT: 'except';
FOR: 'for';
FROM: 'from';
INTO: 'into';
IN: 'in';
LIKE: 'like';
MINUS: 'minus';
NOTWITHSTANDING: 'notwithstanding';
OFF: 'off';
OF: 'of';
ONTO: 'onto';
ON: 'on';
OUTSIDE: 'outside';
OUT: 'out';
OVER: 'over';
PER: 'per';
TO: 'to';
SAVE: 'save';
SHORT: 'short';
SINCE: 'since';
TILL: 'till';
TIL: 'til';
THROUGH: 'through';
TOWARDS: 'towards';
TOWARD: 'toward';
UNDER: 'under';
UNLIKE: 'unlike';
UNTIL: 'until';
UPON: 'upon';
UP: 'up';
WITHIN: 'within';
WITHOUT: 'without';
WITH: 'with';

// pronouns
I: 'i';
YOU: 'you';
WE: 'we';
THEY: 'they';
HE: 'he';
SHE: 'she';
YALL: 'yall' | 'y\'all';
IT: 'it';

// Generic
UNKNOWN_WORD: ('a'..'z' | 'A'..'Z' | '0'..'9' | '-')+;
WHITE_SPACE_AND_IGNORED_SYMBOLS: . -> skip;