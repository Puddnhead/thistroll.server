package com.thistroll.troll.impl;

import com.thistroll.troll.api.SpeechType;
import com.thistroll.troll.api.SpeechTypeResolver;
import com.thistroll.troll.grammar.SpeechLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.UnbufferedCharStream;

import java.io.StringReader;

/**
 * Implementation for {@link SpeechTypeResolver}
 *
 * Created by MVW on 10/5/2017.
 */
public class SpeechTypeResolverImpl implements SpeechTypeResolver {

    @Override
    public SpeechType resolve(String speech) {
        CharStream charStream = new UnbufferedCharStream(new StringReader(speech));
        SpeechLexer lexer = new SpeechLexer(charStream);
        return SpeechType.STATEMENT;
    }
}
