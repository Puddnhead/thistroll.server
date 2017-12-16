package com.thistroll.service.rest;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility for formatting raw text for the troll update service
 *
 * Created by MVW on 12/16/2017.
 */
public class TrollSpeechFormatterUtil {

    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/testSpeech.txt"), Charset.forName("UTF-8"));
        lines = lines.stream()
                .map(line -> line.replaceAll("\"", "\\\\\""))
                .collect(Collectors.toList());
        System.out.println("");
        System.out.println(StringUtils.join(lines, "\\n"));
    }

}