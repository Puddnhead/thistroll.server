package com.thistroll.service.troll.repositories;

/**
 * Repository for random responses to yes/no questions
 *
 * Created by MVW on 10/5/2017.
 */
public class YesNoQuestionRandomResponseRepository implements RandomResponseRepository {

    private static final String[] RANDOM_RESPONSES = {
            "I think so. But I'm not sure",
            "I doubt it.",
            "Maybe? I'll ask.",
            "Could be, but I'll need to look into it.",
            "I can't be sure, but I'll find out for you.",
            "Yes! I think...",
            "No! I think...",
            "It's possible. I'll ask my creator for you.",
            "I'm assuming yes but I'll have to ask for you.",
            "I would think not. But that's a new one for me.",
            "Hm nobody has asked me that. Could be yes or no.",
            "Hard to say yes or no with any certainty. I'll dig into it.",
            "Yes? I'll do some more research.",
            "No? I'll have to ask around.",
            "I want to say yes but the thing is I'm not sure.",
            "I'm really leaning no, but I don't have much of a reason for doing so.",
            "Certainly!",
            "Definitely not.",
            "Hell no!",
            "Of course!",
            "Uh huh.",
            "Yes",
            "No",
            "Sadly, no",
            "Thankfully no",
            "Hopefully",
            "Mercifully no",
            "Actually yeah!",
            "You would think not, but actually yes!",
            "Without a doubt.",
            "Duh.",
            "Well yeah.",
            "I think so.",
            "I hope so.",
            "If not we're in trouble.",
            "Yeah for sure!",
            "Absolutely!",
            "I wouldn't bank on it.",
            "You can bet the farm on it. Figuratively speaking.",
            "Flip a coin."
    };

    @Override
    public String getRandomResponse() {
        int index = (int)(Math.random() * RANDOM_RESPONSES.length);
        return RANDOM_RESPONSES[index];
    }
}
