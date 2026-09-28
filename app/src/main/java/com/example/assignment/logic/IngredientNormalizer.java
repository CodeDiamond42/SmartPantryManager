package com.example.assignment.logic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Turns an ingredient name into a standard form so that small differences
 * don't stop two names from matching.
 *
 * Examples:  "Tomatoes" -> "tomato",  "  Cherry  Tomatoes " -> "cherry tomato",
 *            "Berries" -> "berry",    "Bay Leaves" -> "bay leaf"
 *
 * The same method is applied to pantry items AND recipe ingredients, so even if a
 * rule produces an unusual word, both sides still end up with the same result.
 * Consistency matters more than perfect English here.
 */
public final class IngredientNormalizer {

    // Irregular plurals and words that end in "s" but are not plurals.
    private static final Map<String, String> EXCEPTIONS = new HashMap<>();

    static {
        EXCEPTIONS.put("leaves", "leaf");
        EXCEPTIONS.put("loaves", "loaf");
        EXCEPTIONS.put("molasses", "molasses");
        EXCEPTIONS.put("asparagus", "asparagus");
        EXCEPTIONS.put("hummus", "hummus");
        EXCEPTIONS.put("couscous", "couscous");
    }

    private IngredientNormalizer() {
        // Utility class - no instances needed
    }

    /**
     * Lower-cases the name, removes extra spaces and makes the last word singular.
     * Only the last word is changed because it is usually the noun.
     */
    public static String normalize(String rawName) {
        if (rawName == null) {
            return "";
        }
        String cleaned = rawName.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) {
            return cleaned;
        }

        String[] words = cleaned.split(" ");
        int last = words.length - 1;
        words[last] = singularize(words[last]);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(' ');
            }
            result.append(words[i]);
        }
        return result.toString();
    }

    // Applies simple English plural rules to a single word.
    static String singularize(String word) {
        String exception = EXCEPTIONS.get(word);
        if (exception != null) {
            return exception;
        }
        if (word.length() <= 3) {
            return word;                                              // "gas", "pea"
        }
        if (word.endsWith("ies")) {
            return word.substring(0, word.length() - 3) + "y";        // berries -> berry
        }
        if (word.endsWith("oes")) {
            return word.substring(0, word.length() - 2);              // tomatoes -> tomato
        }
        if (word.endsWith("ches") || word.endsWith("shes")
                || word.endsWith("xes") || word.endsWith("sses")) {
            return word.substring(0, word.length() - 2);              // peaches -> peach
        }
        if (word.endsWith("ss") || word.endsWith("us")) {
            return word;                                              // "cress", "citrus"
        }
        if (word.endsWith("s")) {
            return word.substring(0, word.length() - 1);              // eggs -> egg
        }
        return word;
    }
}
