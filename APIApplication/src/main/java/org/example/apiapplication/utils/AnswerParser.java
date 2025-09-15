package org.example.apiapplication.utils;

import java.util.HashMap;
import java.util.Map;

public class AnswerParser {
    public static Map<String, Double> parseAnswer(String answer) {
        Map<String, Double> result = new HashMap<>();

        String[] tokens = answer.split("=;");
        int variablesCount = (int) Math.ceil(tokens.length / 2.0);

        for (int i = 0; i < variablesCount; i++) {
            result.put(tokens[i * 2], Double.parseDouble(tokens[i * 2 + 1]));
        }

        return result;
    }
}
