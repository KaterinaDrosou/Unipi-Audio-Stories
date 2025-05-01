package com.unipi.katerina.unipiaudiostories;

import java.util.HashMap;
import java.util.Map;

public class StoryUtils {
    public static Story getHardcodedStory(String storyId, String language) {
        Map<String, Map<String, String>> stories = new HashMap<>();

        // Populate story translations
        stories.put("story1", Map.of(
                "en", "Jack and the Beanstalk",
                "de", "Jack und die Bohnenranke",
                "it", "Giacomo e il fagiolo magico"
        ));

        // Authors and images for the first story
        String author1 = "Joseph Jacobs";
        String image1 = "jack_fasolia";

        stories.put("story2", Map.of(
                "en", "Beauty and the Beast",
                "de", "Die Schöne und das Biest",
                "it", "La Bella e la Bestia"
        ));

        // Authors and images for the second story
        String author2 = "Gabrielle-Suzanne Barbot de Villeneuve";
        String image2 = "beauty_beast";

        stories.put("story3", Map.of(
                "en", "Hercules and the twelve labors",
                "de", "Herkules und die zwölf Arbeiten",
                "it", "Ercole e le dodici fatiche"
        ));

        // Authors and images for the third story
        String author3 = "Peisander";
        String image3 = "hercules";

        stories.put("story4", Map.of(
                "en", "Snow White and the Seven Dwarfs",
                "de", "Schneewittchen und die sieben Zwerge",
                "it", "Biancaneve e i sette nani"
        ));

        // Authors and images for the fourth story
        String author4 = "The Brothers Grimm";
        String image4 = "snow_white";

        stories.put("story5", Map.of(
                "en", "Little Red Riding Hood",
                "de", "Rotkäppchen",
                "it", "Cappuccetto Rosso"
        ));

        // Authors and images for the fifth story
        String author5 = "Charles Perrault's";
        String image5 = "kokkinoskoufitsa";

        // Return the translated story with hardcoded author and image
        if (stories.containsKey(storyId) && stories.get(storyId).containsKey(language)) {
            String title = stories.get(storyId).get(language);
            String author = "";
            String image = "";

            // Set the correct author and image based on storyId
            if (storyId.equals("story1")) {
                author = author1;
                image = image1;
            } else if (storyId.equals("story2")) {
                author = author2;
                image = image2;
            } else if (storyId.equals("story3")) {
                author = author3;
                image = image3;
            } else if (storyId.equals("story4")) {
                author = author4;
                image = image4;
            } else if (storyId.equals("story5")) {
                author = author5;
                image = image5;
            }

            return new Story(storyId, title, author, image);
        }
        return null;
    }
}