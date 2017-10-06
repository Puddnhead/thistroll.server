package com.thistroll.service.troll.repositories;

/**
 * Repository for providing random responses to open-ended questions
 *
 * Created by MVW on 10/5/2017.
 */
public class OpenEndedQuestionRandomResponseRepository implements RandomResponseRepository {

    static final String[] RANDOM_RESPONSES = {
        "Hmm that I don't know, but come back later and I might!",
        "I'll look into it and get back to you.",
        "I'm going to need some time to figure that one out.",
        "Who knows. I'll look into it for you.",
        "43. According to a cool sci-fi writer the answer is always 43.",
        "I really wish I could help you. I'll ask my creator about it.",
        "Some day I'll have an answer for you, but sadly right now I do not.",
        "Dunno.",
        "Good question! I'll do some research.",
        "I wonder that myself. I'll ask around for you.",
        "I never even thought to think about it. But I think I'll think on it now.",
        "I'm stumped. But I expect I'll be able to find you an answer in a day or two.",
        "If I knew that I'd be super internet popular. Maybe some day.",
        "Don't you know? I'll try to find out for you. Ask me later.",
        "They really don't tell me anything. Or they do but not until after you ask me something I don't know.",
        "I was hungover when they taught that one. But I'll make a note of it and try to find out.",
        "I'm not sure, but I do have a learning mechanism now, so I'll know soon enough.",
        "I forget... Come back later?",
        "I hate to disappoint, but that's not in my memory banks. I'll ask a guy when I get a chance.",
        "You're the first person to ask me that! I'll ask my creator for you."
    };

    @Override
    public String getRandomResponse() {
        int index = (int)(Math.random() * RANDOM_RESPONSES.length);
        return RANDOM_RESPONSES[index];
    }
}
