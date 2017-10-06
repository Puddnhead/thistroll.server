package com.thistroll.service.troll.repositories;

/**
 * Repository for random responses to statements
 *
 * Created by MVW on 10/5/2017.
 */
public class StatementRandomResponseRepository implements RandomResponseRepository {

    private static final String[] RANDOM_RESPONSES = {
        "No kidding?",
        "That's fascinating. Really.",
        "I learn something new every day.",
        "Tell me more!",
        "Wow you're really intriguing!",
        "I've never heard that before.",
        "Oh really?",
        "Huh.",
        "You don't say?",
        "And?",
        "What's your point?",
        "Say what?",
        "Sorry I was busy doing something else. What was that?",
        "What makes you think that?",
        "Are you sure?",
        "No way!",
        "Yeah I know.",
        "Duh.",
        "Something tells me you read that on a cereal box.",
        "That has academy-award-winning script written all over it.",
        "I thought so.",
        "If I had a nickel for every time I heard that one. But they won't let me have nickels.",
        "You should write a song about that.",
        "Did you read that in a book?",
        "Why?",
        "I wonder if I should believe you.",
        "I farted.",
        "Not surprising.",
        "Fascinating. Truly.",
        "Whoa deja vu.",
        "Bodacious!",
        "Cowabunga dude",
        "Bullshit.",
        "Yeah right.",
        "I guess.",
        "I just don't know what to say. But I'll ask my creator and next time I'll be ready.",
        "I'm speechless.",
        "Thanks for telling me.",
        "I appreciate you confiding in me.",
        "Your secret is safe with me.",
        "Huh. Never woulda thunk it.",
        "Guess I still have a few things to learn.",
        "How thought-provoking",
        "Oh. My. God.",
        "Captivating stuff.",
        "Sheesh.",
        "Is that all you've got? I'm kind of busy you know. Kidding....",
        "<burp>",
        "VICTORY FOR THE FORCES OF DEMOCRATIC FREEDOM!"
    };

    @Override
    public String getRandomResponse() {
        int index = (int)(Math.random() * RANDOM_RESPONSES.length);
        return RANDOM_RESPONSES[index];
    }
}
