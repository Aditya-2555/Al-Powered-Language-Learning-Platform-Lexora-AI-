package com.lingua.service;

import java.util.*;

public class VocabularyBank {
    private static final Random random = new Random();
    
    // Map<LanguageCode, Map<Category, List<String[]>>>
    // String[] = {English (native), Target Language}
    private static final Map<String, Map<String, List<String[]>>> bank = new HashMap<>();

    static {
        Map<String, List<String[]>> es = new HashMap<>();
        es.put("[Food]", Arrays.asList(new String[]{"apple", "manzana"}, new String[]{"bread", "pan"}, new String[]{"cheese", "queso"}, new String[]{"water", "agua"}, new String[]{"milk", "leche"}, new String[]{"salad", "ensalada"}));
        es.put("[Family_Member]", Arrays.asList(new String[]{"mother", "madre"}, new String[]{"father", "padre"}, new String[]{"brother", "hermano"}, new String[]{"sister", "hermana"}, new String[]{"grandmother", "abuela"}));
        es.put("[Category_Travel]", Arrays.asList(new String[]{"hotel", "hotel"}, new String[]{"airport", "aeropuerto"}, new String[]{"train", "tren"}, new String[]{"ticket", "boleto"}, new String[]{"passport", "pasaporte"}));
        es.put("[Greeting]", Arrays.asList(new String[]{"Hello", "Hola"}, new String[]{"Good morning", "Buenos días"}, new String[]{"Good evening", "Buenas tardes"}, new String[]{"Goodbye", "Adiós"}));
        es.put("[Verb]", Arrays.asList(new String[]{"eat", "comer"}, new String[]{"drink", "beber"}, new String[]{"run", "correr"}, new String[]{"sleep", "dormir"}, new String[]{"walk", "caminar"}, new String[]{"read", "leer"}));
        bank.put("es", es);
        
        Map<String, List<String[]>> fr = new HashMap<>();
        fr.put("[Food]", Arrays.asList(new String[]{"apple", "pomme"}, new String[]{"bread", "pain"}, new String[]{"cheese", "fromage"}, new String[]{"water", "l'eau"}, new String[]{"milk", "lait"}, new String[]{"salad", "salade"}));
        fr.put("[Family_Member]", Arrays.asList(new String[]{"mother", "mère"}, new String[]{"father", "père"}, new String[]{"brother", "frère"}, new String[]{"sister", "sœur"}, new String[]{"grandfather", "grand-père"}));
        fr.put("[Category_Travel]", Arrays.asList(new String[]{"hotel", "hôtel"}, new String[]{"airport", "aéroport"}, new String[]{"train", "train"}, new String[]{"ticket", "billet"}, new String[]{"passport", "passeport"}));
        fr.put("[Greeting]", Arrays.asList(new String[]{"Hello", "Bonjour"}, new String[]{"Good morning", "Bonjour"}, new String[]{"Good evening", "Bonsoir"}, new String[]{"Goodbye", "Au revoir"}));
        fr.put("[Verb]", Arrays.asList(new String[]{"eat", "manger"}, new String[]{"drink", "boire"}, new String[]{"run", "courir"}, new String[]{"sleep", "dormir"}, new String[]{"walk", "marcher"}, new String[]{"read", "lire"}));
        bank.put("fr", fr);
        
        Map<String, List<String[]>> de = new HashMap<>();
        de.put("[Food]", Arrays.asList(new String[]{"apple", "Apfel"}, new String[]{"bread", "Brot"}, new String[]{"cheese", "Käse"}, new String[]{"water", "Wasser"}, new String[]{"milk", "Milch"}, new String[]{"salad", "Salat"}));
        de.put("[Family_Member]", Arrays.asList(new String[]{"mother", "Mutter"}, new String[]{"father", "Vater"}, new String[]{"brother", "Bruder"}, new String[]{"sister", "Schwester"}, new String[]{"uncle", "Onkel"}));
        de.put("[Category_Travel]", Arrays.asList(new String[]{"hotel", "Hotel"}, new String[]{"airport", "Flughafen"}, new String[]{"train", "Zug"}, new String[]{"ticket", "Ticket"}, new String[]{"passport", "Reisepass"}));
        de.put("[Greeting]", Arrays.asList(new String[]{"Hello", "Hallo"}, new String[]{"Good morning", "Guten Morgen"}, new String[]{"Good evening", "Guten Abend"}, new String[]{"Goodbye", "Tschüss"}));
        de.put("[Verb]", Arrays.asList(new String[]{"eat", "essen"}, new String[]{"drink", "trinken"}, new String[]{"run", "rennen"}, new String[]{"sleep", "schlafen"}, new String[]{"walk", "gehen"}, new String[]{"read", "lesen"}));
        bank.put("de", de);
    }

    public static String[] getRandomPair(String langCode, String category) {
        langCode = langCode.toLowerCase();
        if (bank.containsKey(langCode) && bank.get(langCode).containsKey(category)) {
            List<String[]> list = bank.get(langCode).get(category);
            return list.get(random.nextInt(list.size()));
        }
        return new String[]{"placeholder_en", "placeholder_tgt"};
    }
    
    public static String[] applyTemplate(String textEn, String textTgt, String langCode) {
        String resolvedEn = textEn;
        String resolvedTgt = textTgt;
        
        String[] categories = {"[Food]", "[Family_Member]", "[Greeting]", "[Category_Travel]", "[Verb]"};
        
        for (String cat : categories) {
            if (resolvedEn.contains(cat) || resolvedTgt.contains(cat)) {
                String[] pair = getRandomPair(langCode, cat);
                resolvedEn = resolvedEn.replaceFirst(cat.replace("[", "\\[").replace("]", "\\]"), pair[0]);
                resolvedTgt = resolvedTgt.replaceFirst(cat.replace("[", "\\[").replace("]", "\\]"), pair[1]);
            }
        }
        
        return new String[]{resolvedEn, resolvedTgt};
    }

    public static List<String> getAllTargetWords(String langCode) {
        List<String> words = new ArrayList<>();
        langCode = langCode.toLowerCase();
        if (bank.containsKey(langCode)) {
            for (List<String[]> pairs : bank.get(langCode).values()) {
                for (String[] pair : pairs) {
                    words.add(pair[1]);
                }
            }
        }
        return words;
    }
}
