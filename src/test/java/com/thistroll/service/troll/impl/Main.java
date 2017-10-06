package com.thistroll.service.troll.impl;

import com.thistroll.service.troll.api.SpeechTypeResolver;

import java.util.Scanner;

/**
 * Utility for testing the speech type resolver from the command line
 *
 * Created by MVW on 10/5/2017.
 */
public class Main {

    private static SpeechTypeResolver speechTypeResolver = new SpeechTypeResolverImpl(true);

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        String input = null;

        System.out.println("Input: ");

        while ((input = keyboard.nextLine()) != "\n") {
            System.out.println(speechTypeResolver.resolve(input));
        }
    }
}
