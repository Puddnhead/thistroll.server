package com.thistroll.service.troll.impl;

import com.thistroll.service.troll.api.SpeechType;
import com.thistroll.service.troll.api.SpeechTypeResolver;
import com.thistroll.service.troll.grammar.SpeechLexer;
import com.thistroll.service.troll.grammar.SpeechParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Implementation for {@link SpeechTypeResolver}
 *
 * Created by MVW on 10/5/2017.
 */
public class SpeechTypeResolverImpl implements SpeechTypeResolver {

    private boolean printTree = false;

    SpeechTypeResolverImpl(boolean printTree) {
        this.printTree = printTree;
    }

    @Override
    public SpeechType resolve(String speech) {
        CharStream charStream;
        InputStream stream = new ByteArrayInputStream(speech.getBytes(StandardCharsets.UTF_8));

        try {
            charStream = CharStreams.fromStream(stream, StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            System.out.println("Search Type Resolver I/O error - defaulting to STATEMENT");
            return SpeechType.STATEMENT;
        }
        SpeechLexer lexer = new SpeechLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SpeechParser parser = new SpeechParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.sentence();

        if (printTree) {
            System.out.println(tree.toStringTree(parser));
        }

        ParseTreeWalker walker = new ParseTreeWalker();
        SpeechTypeListener listener = new SpeechTypeListener();
        walker.walk(listener, tree);

        return listener.getSpeechType();
    }
}
