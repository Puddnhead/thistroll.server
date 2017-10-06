grammar Speech;

import SpeechLexerRules;

sentence: (question | statement);

question: open_ended_question | yes_no_question;

open_ended_question:
    preposition? HOW statement QUESTION_MARK?
    | preposition? WHAT statement QUESTION_MARK?
    | preposition? WHEN statement QUESTION_MARK?
    | preposition? WHERE statement QUESTION_MARK?
    | preposition? WHICH statement QUESTION_MARK?
    | preposition? WHOSE statement QUESTION_MARK?
    | preposition? WHOM statement QUESTION_MARK?
    | preposition? WHO statement QUESTION_MARK?
    | preposition? WHY statement QUESTION_MARK?
    | conditional_clause open_ended_question
    | prepositional_clause open_ended_question;

yes_no_question:
    statement QUESTION_MARK
    | AM statement QUESTION_MARK?
    | ARE statement QUESTION_MARK?
    | CAN statement QUESTION_MARK?
    | COULD statement QUESTION_MARK?
    | DID statement QUESTION_MARK?
    | DO statement QUESTION_MARK?
    | DOES statement QUESTION_MARK?
    | IS statement QUESTION_MARK?
    | OUGHT statement QUESTION_MARK?
    | SHOULD statement QUESTION_MARK?
    | WAS statement QUESTION_MARK?
    | WERE statement QUESTION_MARK?
    | WILL statement QUESTION_MARK?
    | WOULD statement QUESTION_MARK?
    | conditional_clause yes_no_question
    | prepositional_clause yes_no_question;

conditional_clause: IF statement;
prepositional_clause: preposition statement;

statement: word+;

word:
    preposition
    | pronoun
    | question_word
    | UNKNOWN_WORD;

preposition:
    ABOARD
    | ABOUT
    | ABOVE
    | ABSENT
    | ACROSS
    | AFTER
    | AGAINST
    | ALONGSIDE
    | ALONG
    | AMID
    | AMONGST
    | AMONG
    | AROUND
    | ASTRIDE
    | AS
    | ATOP
    | BEFORE
    | BELOW
    | BESIDES
    | BESIDE
    | BETWEEN
    | BEYOND
    | BY
    | DESPITE
    | DOWN
    | DURING
    | EXCEPT
    | FOR
    | FROM
    | INTO
    | IN
    | LIKE
    | MINUS
    | NOTWITHSTANDING
    | OFF
    | OF
    | ONTO
    | ON
    | OUTSIDE
    | OUT
    | OVER
    | PER
    | TO
    | SAVE
    | SHORT
    | SINCE
    | TIL
    | TIL
    | THROUGH
    | TOWARDS
    | TOWARD
    | UNDER
    | UNLIKE
    | UNTIL
    | UPON
    | UP
    | WITHIN
    | WITHOUT
    | WITH;

question_word:
    // question words
      AM
      | ARE
      | CAN
      | COULD
      | DO
      | DOES
      | DID
      | HOW
      | IS
      | OUGHT
      | SHOULD
      | WAS
      | WERE
      | WHAT
      | WHEN
      | WHERE
      | WHICH
      | WHO
      | WHOM
      | WHOSE
      | WHY
      | WILL
      | WOULD;

pronoun:
    I
    | YOU
    | WE
    | THEY
    | HE
    | SHE
    | YALL
    | IT;